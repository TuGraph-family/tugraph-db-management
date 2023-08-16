cmake_minimum_required(VERSION 3.25)
project(log_demo)

set(CMAKE_CXX_STANDARD 17)

# include_directories(/data/lsl/glog/build/include/)

# link_directories(/data/lsl/glog/build/lib64/)

include_directories(/data/lsl/boost_1_68_0/stage/lib /data/lsl/fma-common)
link_libraries(-lstdc++fs)
# link_directories(/data/lsl/boost_1_68_0/stage/lib)

# boost
set(BOOST_ROOT /data/lsl/boost_1_68_0)
set(Boost_NO_SYSTEM_PATHS ON)
# find_package(Boost 1.68 REQUIRED COMPONENTS log log_setup)
find_package(Boost 1.68 REQUIRED COMPONENTS log)
add_definitions(-DBOOST_LOG_DYN_LINK)

if(Boost_FOUND)
    include_directories(${Boost_INCLUDE_DIRS})

    MESSAGE( STATUS "Boost_INCLUDE_DIRS = ${Boost_INCLUDE_DIRS}.")
    MESSAGE( STATUS "Boost_LIBRARIES = ${Boost_LIBRARIES}.")
    MESSAGE( STATUS "Boost_LIB_VERSION = ${Boost_LIB_VERSION}.")

    add_executable(audit_log_demo test.cpp)
    target_link_libraries (audit_log_demo ${Boost_LIBRARIES})
endif()


# target_link_libraries(log_demo glog)
