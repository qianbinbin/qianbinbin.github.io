cmake_minimum_required(VERSION 3.20)

project(googletest-download NONE)

include(ExternalProject)
ExternalProject_Add(
        googletest
        GIT_REPOSITORY https://github.com/google/googletest.git
        GIT_TAG main
        SOURCE_DIR "${CMAKE_BINARY_DIR}/googletest-src"
        BINARY_DIR "${CMAKE_BINARY_DIR}/googletest-build"
        CONFIGURE_COMMAND ""
        BUILD_COMMAND ""
        INSTALL_COMMAND ""
        TEST_COMMAND ""
)
