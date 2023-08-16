#include "logger.h"

enum
{
    THREAD_COUNT = 10,
    LOG_RECORDS_TO_WRITE = 10
};

// This function is executed in a separate thread
void thread_foo_0()
{
    neo_logger::AuditLogger::GetInstance().WriteLog();
    boost::this_thread::sleep(boost::posix_time::seconds(20));
}

// This function is executed in a separate thread
void thread_foo_1()
{
    neo_logger::AuditLogger::GetInstance().ReadLog();
    boost::this_thread::sleep(boost::posix_time::seconds(20));
}

int main(int argc, char* argv[])
{
    neo_logger::LoggerManager::GetInstance().Init("logs/", neo_logger::severity_level::DEBUG);
    neo_logger::AuditLogger::GetInstance().Init("logs/");
    boost::thread_group threads;
    for (unsigned int i = 0; i < THREAD_COUNT; ++i)
        threads.create_thread(&thread_foo_0);
    for (unsigned int i = 0; i < THREAD_COUNT; ++i)
        threads.create_thread(&thread_foo_1);
    threads.join_all();
    return 0;
}