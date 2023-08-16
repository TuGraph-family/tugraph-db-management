// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

// A client sending requests to server every 1 second.

#include <gflags/gflags.h>
#include <butil/logging.h>
#include <butil/time.h>
#include <brpc/channel.h>
#include "tugraph_management.pb.h"

DEFINE_string(attachment, "", "Carry this along with requests");
DEFINE_string(protocol, "baidu_std", "Protocol type. Defined in src/brpc/options.proto");
DEFINE_string(connection_type, "", "Connection type. Available values: single, pooled, short");
DEFINE_string(server, "0.0.0.0:8000", "IP Address of server");
DEFINE_string(load_balancer, "", "The algorithm for load balancing");
DEFINE_int32(timeout_ms, 100, "RPC timeout in milliseconds");
DEFINE_int32(max_retry, 3, "Max retries(not including the first RPC)");
DEFINE_int32(interval_ms, 1000, "Milliseconds between consecutive requests");

int test_create_job_request(com::antgroup::tugraph::JobManagementService_Stub& stub, int& log_id) {
    // We will receive response synchronously, safe to put variables
    // on stack.
    com::antgroup::tugraph::JobManagementRequest request;
    com::antgroup::tugraph::JobManagementResponse response;
    brpc::Controller cntl;

    request.set_db_host("127.0.0.1");
    request.set_db_port("8888");

    // test create_job_request
    request.set_allocated_create_job_request(new com::antgroup::tugraph::CreateJobRequest());
    request.mutable_create_job_request()->set_start_time("2023-08-08 10:52:57.200");
    request.mutable_create_job_request()->set_period("IMMEDIATE");
    request.mutable_create_job_request()->set_procedure_name("Khop_test");
    request.mutable_create_job_request()->set_procedure_type("Khop");
    request.mutable_create_job_request()->set_creator("lsl");
    request.mutable_create_job_request()->set_create_time("2023-08-08 10:52:57.200");


    cntl.set_log_id(log_id ++);  // set by user
    // Set attachment which is wired to network directly instead of
    // being serialized into protobuf messages.
    cntl.request_attachment().append(FLAGS_attachment);

    // Because `done'(last parameter) is NULL, this function waits until
    // the response comes back or error occurs(including timedout).
    stub.handleRequest(&cntl, &request, &response, NULL);
    if (!cntl.Failed()) {
        LOG(INFO) << "Received response from " << cntl.remote_side()
            << " to " << cntl.local_side()
            << ": " << response.error_code() << " (attached="
            << cntl.response_attachment() << ")"
            << " latency=" << cntl.latency_us() << "us";
        return response.create_job_response().job_id();
    } else {
        LOG(WARNING) << cntl.ErrorText();
        return -1;
    }
}

void test_update_job_request(com::antgroup::tugraph::JobManagementService_Stub& stub, int& log_id, int job_id) {
    // We will receive response synchronously, safe to put variables
    // on stack.
    com::antgroup::tugraph::JobManagementRequest request;
    com::antgroup::tugraph::JobManagementResponse response;
    brpc::Controller cntl;

    request.set_db_host("127.0.0.1");
    request.set_db_port("8888");

    // test create_job_request
    request.set_allocated_update_job_request(new com::antgroup::tugraph::UpdateJobRequest());
    request.mutable_update_job_request()->set_job_id(job_id);
    request.mutable_update_job_request()->set_status("SUCCESS");
    request.mutable_update_job_request()->set_runtime("100");
    request.mutable_update_job_request()->set_result("this is only a test of result");

    cntl.set_log_id(log_id ++);  // set by user
    // Set attachment which is wired to network directly instead of
    // being serialized into protobuf messages.
    cntl.request_attachment().append(FLAGS_attachment);

    // Because `done'(last parameter) is NULL, this function waits until
    // the response comes back or error occurs(including timedout).
    stub.handleRequest(&cntl, &request, &response, NULL);
    if (!cntl.Failed()) {
        LOG(INFO) << "Received response from " << cntl.remote_side()
            << " to " << cntl.local_side()
            << ": " << response.error_code() << " (attached="
            << cntl.response_attachment() << ")"
            << " latency=" << cntl.latency_us() << "us";
    } else {
        LOG(WARNING) << cntl.ErrorText();
    }
}

int main(int argc, char* argv[]) {
    // Parse gflags. We recommend you to use gflags as well.
    GFLAGS_NS::ParseCommandLineFlags(&argc, &argv, true);

    // A Channel represents a communication line to a Server. Notice that
    // Channel is thread-safe and can be shared by all threads in your program.
    brpc::Channel channel;

    // Initialize the channel, NULL means using default options.
    brpc::ChannelOptions options;
    options.protocol = FLAGS_protocol;
    options.connection_type = FLAGS_connection_type;
    options.timeout_ms = FLAGS_timeout_ms/*milliseconds*/;
    options.max_retry = FLAGS_max_retry;
    if (channel.Init(FLAGS_server.c_str(), FLAGS_load_balancer.c_str(), &options) != 0) {
        LOG(ERROR) << "Fail to initialize channel";
        return -1;
    }

    // Normally, you should not call a Channel directly, but instead construct
    // a stub Service wrapping it. stub can be shared by all threads as well.
    com::antgroup::tugraph::JobManagementService_Stub stub(&channel);

    // Send a request and wait for the response every 1 second.
    int log_id = 0;
    while (!brpc::IsAskedToQuit()) {
        int job_id = test_create_job_request(stub, log_id);
        usleep(FLAGS_interval_ms * 1000L);
        test_update_job_request(stub, log_id, job_id);
        usleep(FLAGS_interval_ms * 1000L);
    }

    LOG(INFO) << "TuGraphManagementClient is going to quit";
    return 0;
}
