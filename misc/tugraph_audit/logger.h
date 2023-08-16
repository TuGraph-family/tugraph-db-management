/**
 * Copyright 2022 AntGroup CO., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */

#pragma once

#include <stdexcept>
#include <string>
#include <iostream>
#include <boost/date_time/posix_time/posix_time.hpp>
#include <boost/log/common.hpp>
#include <boost/log/expressions.hpp>
#include <boost/log/attributes.hpp>
#include <boost/log/sinks.hpp>
#include <boost/log/sources/logger.hpp>
#include <boost/log/utility/manipulators/add_value.hpp>
#include <boost/phoenix/bind/bind_function.hpp>
#include <boost/core/null_deleter.hpp>

#include "json.hpp"
#include "fma-common/file_system.h"
#include "fma-common/fma_stream.h"
#include "fma-common/stream_base.h"

namespace neo_logger {

// Define log macro
#define GENERAL_LOG(LEVEL) BOOST_LOG_SEV(::neo_logger::general_logger::get(), \
::neo_logger::severity_level::LEVEL)

#define GENERAL_LOG_STREAM(LEVEL, CLASS) BOOST_LOG_SEV(::neo_logger::general_logger::get(), \
  ::neo_logger::severity_level::LEVEL) \
  << ::neo_logger::logging::add_value("Class", CLASS)

#define DEBUG_LOG(LEVEL) BOOST_LOG_SEV(::neo_logger::debug_logger::get(), \
  ::neo_logger::severity_level::LEVEL) \
  << ::neo_logger::logging::add_value("Line", __LINE__) \
  << ::neo_logger::logging::add_value("File", __FILE__)       \
  << ::neo_logger::logging::add_value("Function", __FUNCTION__) \

#define DEBUG_LOG_STREAM(LEVEL, CLASS) BOOST_LOG_SEV(::neo_logger::debug_logger::get(), \
  ::neo_logger::severity_level::LEVEL) \
  << ::neo_logger::logging::add_value("Line", __LINE__) \
  << ::neo_logger::logging::add_value("File", __FILE__)       \
  << ::neo_logger::logging::add_value("Function", __FUNCTION__) \
  << ::neo_logger::logging::add_value("Class", CLASS)

#define AUDIT_LOG() BOOST_LOG(::neo_logger::audit_logger::get())

namespace logging = boost::log;
namespace attrs = boost::log::attributes;
namespace src = boost::log::sources;
namespace sinks = boost::log::sinks;
namespace expr = boost::log::expressions;
namespace keywords = boost::log::keywords;

using boost::shared_ptr;
using json = nlohmann::json;

typedef sinks::synchronous_sink< sinks::text_file_backend > file_sink;
typedef sinks::synchronous_sink< sinks::text_ostream_backend > console_sink;

BOOST_LOG_ATTRIBUTE_KEYWORD(log_type_attr, "LogType", std::string)

enum severity_level {
    TRACE,
    DEBUG,
    INFO,
    WARNING,
    ERROR,
    FATAL
};

class LoggerManager {
 private:
    std::string log_dir_;
    std::string rotation_target_dir_;
    std::string history_general_log_dir_;
    std::string history_debug_log_dir_;
    severity_level level_;
    int rotation_size_;
    boost::shared_ptr< file_sink > general_sink_;
    boost::shared_ptr< file_sink > debug_sink_;
    boost::shared_ptr< console_sink > console_sink_;
    bool global_inited_ = false;

    static std::string level_to_string(logging::value_ref< severity_level > const& level) {
      switch (level.get()) {
      case TRACE:
          return "TRACE";
      case DEBUG:
          return "DEBUG";
      case INFO:
          return "INFO";
      case WARNING:
          return "WARNING";
      case ERROR:
          return "ERROR";
      case FATAL:
          return "FATAL";
      default:
          return "Invalid severity level";
      }
    }

    static void general_formatter(logging::record_view const& rec,
                                  logging::formatting_ostream& strm) {
      strm << "[" << logging::extract< boost::posix_time::ptime >("TimeStamp", rec) << "] ";
      strm << "[" << level_to_string(logging::extract< severity_level >("Severity", rec)) << "] ";
      logging::value_ref< std::string > class_name = logging::extract< std::string >("Class", rec);
      if (class_name) strm << "[" << class_name << "] ";
      strm << "- "<< rec[expr::smessage];
    }

    static void debug_formatter(logging::record_view const& rec,
                                logging::formatting_ostream& strm) {
      strm << "[" << logging::extract< boost::posix_time::ptime >("TimeStamp", rec) << "] ";
      if (LoggerManager::GetInstance().GetLevel() <= severity_level::DEBUG) {
          strm << "[" << logging::extract< attrs::current_thread_id::value_type >("ThreadID", rec)
          << "] ";
      }
      strm << "[" << level_to_string(logging::extract< severity_level >("Severity", rec)) << "] ";
      logging::value_ref< std::string > class_name = logging::extract< std::string >("Class", rec);
      if (class_name) strm << "[" << class_name << "] ";
      if (LoggerManager::GetInstance().GetLevel() <= severity_level::DEBUG) {
          strm << "[" << logging::extract<std::string>("File", rec) << " : "
          << logging::extract<std::string>("Function", rec) << " : "
          << logging::extract<int>("Line", rec)
          << "] ";
      }
      strm << "- "<< rec[expr::smessage];
    }

    static void console_formatter(logging::record_view const& rec,
                                  logging::formatting_ostream& strm) {
      logging::value_ref< std::string > log_type = logging::extract< std::string >("LogType", rec);
      if (log_type.get() == "debug") {
          debug_formatter(rec, strm);
      } else if (log_type.get() == "general") {
          general_formatter(rec, strm);
      }
    }

 public:
    /**
     * @brief   Init LoggerManager. Set log directory, log filtering level.
     *
     * @param   log_dir   The log directory.
     * @param   level     The log filtering level.
     */
    void Init(std::string log_dir = "logs/", severity_level level = severity_level::INFO) {
      // Set up log directory
      log_dir_ = log_dir;
      level_ = level;
      rotation_size_ = 5 * 1024 * 1024;
      rotation_target_dir_ = log_dir_ + "history_logs/";
      history_general_log_dir_ = rotation_target_dir_ + "general_logs/";
      history_debug_log_dir_ = rotation_target_dir_ + "debug_logs/";

      // Set up sink for general log
      general_sink_ = boost::shared_ptr< file_sink > (new file_sink(
          keywords::file_name = log_dir_ + "general.log",
          keywords::open_mode = std::ios_base::out | std::ios_base::app,
          keywords::enable_final_rotation = false,
          keywords::auto_flush = true,
          keywords::rotation_size = rotation_size_));
      general_sink_->locked_backend()->set_file_collector(sinks::file::make_collector(
          keywords::target = history_general_log_dir_));
      general_sink_->locked_backend()->scan_for_files();
      general_sink_->set_filter(log_type_attr == "general");
      general_sink_->set_formatter(&this->general_formatter);

      // Set up sink for debug log
      debug_sink_ = boost::shared_ptr< file_sink > (new file_sink(
          keywords::file_name = log_dir_ + "debug.log",
          keywords::open_mode = std::ios_base::out | std::ios_base::app,
          keywords::enable_final_rotation = false,
          keywords::auto_flush = true,
          keywords::rotation_size = rotation_size_));
      debug_sink_->locked_backend()->set_file_collector(sinks::file::make_collector(
          keywords::target = history_debug_log_dir_));
      debug_sink_->locked_backend()->scan_for_files();
      debug_sink_->set_filter(expr::attr< severity_level >("Severity") >= level_ &&
          log_type_attr == "debug");
      debug_sink_->set_formatter(&this->debug_formatter);

      // Set up sink for console log
      console_sink_ = boost::shared_ptr< console_sink > (new console_sink());
      console_sink_->locked_backend()->add_stream(
          boost::shared_ptr< std::ostream >(&std::clog, boost::null_deleter()));
      console_sink_->locked_backend()->auto_flush(true);
      console_sink_->set_filter(expr::attr< severity_level >("Severity") >= level_);
      console_sink_->set_formatter(&this->console_formatter);

      // Add sinks to log core
      logging::core::get()->remove_all_sinks();
      if (log_dir_ == "") {
          logging::core::get()->add_sink(console_sink_);
      } else {
          logging::core::get()->add_sink(general_sink_);
          logging::core::get()->add_sink(debug_sink_);
      }

      // Add some attributes too
      if (!global_inited_) {
          logging::core::get()->add_global_attribute("TimeStamp", attrs::local_clock());
          logging::core::get()->add_global_attribute("ThreadID", attrs::current_thread_id());
      }

      global_inited_ = true;
    }

    /**
     * @brief   Set the log filtering level.
     *
     * @param   level     The log filtering level.
     */
    void SetLevel(severity_level level) {
      level_ = level;
      general_sink_->set_filter(expr::attr< severity_level >("Severity") >= level_ &&
          log_type_attr == "general");
      debug_sink_->set_filter(expr::attr< severity_level >("Severity") >= level_ &&
          log_type_attr == "debug");
      console_sink_->set_filter(expr::attr< severity_level >("Severity") >= level_);
    }

    /**
     * @brief   Get current log filtering level.
     *
     * @returns   current log filtering level.
     */
    severity_level GetLevel() { return level_; }

    /**
     * @brief   Check if logger manager is already inited.
     *
     * @returns   true if inited, flase if not.
     */
    bool IsInited() { return global_inited_; }

    /**
     * @brief   Get a instance of LoggerManager class.
     *
     * @returns   a instance of LoggerManager class.
     */
    static LoggerManager& GetInstance() {
      static LoggerManager instance;
      return instance;
    }
};

// Global logger init
BOOST_LOG_INLINE_GLOBAL_LOGGER_INIT(general_logger, src::severity_logger_mt< severity_level >) {
  src::severity_logger_mt< severity_level > lg;
  attrs::constant< std::string > general_type("general");
  lg.add_attribute("LogType", general_type);
  return lg;
}

BOOST_LOG_INLINE_GLOBAL_LOGGER_INIT(debug_logger, src::severity_logger_mt< severity_level >) {
  src::severity_logger_mt< severity_level > lg;
  attrs::constant< std::string > debug_type("debug");
  lg.add_attribute("LogType", debug_type);

  // Init empty console log first if not inited
  if (!LoggerManager::GetInstance().IsInited()) {
      boost::shared_ptr< console_sink > empty_sink =
          boost::shared_ptr< console_sink > (new console_sink());
      empty_sink->locked_backend()->add_stream(
          boost::shared_ptr< std::ostream >(&std::clog, boost::null_deleter()));
      empty_sink->locked_backend()->auto_flush(true);
      logging::core::get()->add_sink(empty_sink);
  }

  return lg;
}

BOOST_LOG_INLINE_GLOBAL_LOGGER_INIT(audit_logger, src::logger_mt) {
  src::logger_mt lg;
  attrs::constant< std::string > audit_type("audit");
  lg.add_attribute("LogType", audit_type);
  return lg;
}

class AuditLogger {
 private:
    std::string log_dir_;
    int rotation_size_;
    boost::shared_ptr< file_sink > audit_sink_;
    bool global_inited_ = false;

 public:
    void Init(std::string log_dir = "logs/") {
        if(!global_inited_) {
            // Set up log directory
            log_dir_ = log_dir + "audit_logs/";
            rotation_size_ = 5 * 1024 * 1024;

            // Set up sink for debug log
            audit_sink_ = boost::shared_ptr< file_sink > (new file_sink(
                keywords::file_name = log_dir_ + "audit_%Y-%m-%d_%H-%M-%S.log",
                keywords::auto_flush = true,
                keywords::rotation_size = rotation_size_));
            audit_sink_->locked_backend()->set_file_collector(sinks::file::make_collector(
                keywords::target = log_dir_));
            audit_sink_->locked_backend()->scan_for_files();
            audit_sink_->set_filter(log_type_attr == "audit");

            // Add sinks to log core
            logging::core::get()->add_sink(audit_sink_);

            global_inited_ = true;
        }
    }

    void WriteLog() {
        json test_json;
        std::string content(1000, 'h');
        test_json["content"] = content;
        for (int i = 0; i < 5; i++) {
            AUDIT_LOG() << test_json.dump();
            AUDIT_LOG() << test_json.dump();
        }
    }

    void ReadLog() {
        // DEBUG_LOG(DEBUG) << "start to read.";
        auto& fs = fma_common::FileSystem::GetFileSystem(log_dir_);
        auto files = fs.ListFiles(log_dir_, nullptr);
        std::sort(files.begin(), files.end());
        for (size_t f_i = 0; f_i < files.size(); f_i++) {
            std::string f = fs.GetFileName(files[f_i]);
            DEBUG_LOG(DEBUG) << f;
            fma_common::InputFmaStream input(files[f_i]);
            fma_common::StreamLineReader reader(input);
            auto lines = reader.ReadAllLines();
            DEBUG_LOG(DEBUG) << lines.size();
            for (auto& line : lines) {
                DEBUG_LOG(DEBUG) << line;
            }
        }
    }

    static AuditLogger& GetInstance() {
        static AuditLogger instance;
        return instance;
    }
};
}  // namespace neo_logger
