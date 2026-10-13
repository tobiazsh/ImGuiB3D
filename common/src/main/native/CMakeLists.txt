cmake_minimum_required(VERSION 3.15)

if(APPLE)
    set(CMAKE_OSX_DEPLOYMENT_TARGET "11.0" CACHE STRING "Minimum macOS version")
endif()

project(ImGuiB3DNative LANGUAGES CXX)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_CXX_VISIBILITY_PRESET hidden)
set(CMAKE_VISIBILITY_INLINES_HIDDEN ON)

set(CMAKE_MSVC_RUNTIME_LIBRARY "MultiThreaded$<$<CONFIG:Debug>:Debug>")

find_package(JNI REQUIRED)

if(NOT DEFINED JNI_GENERATED_HEADERS_DIR)
    message(FATAL_ERROR
        "JNI_GENERATED_HEADERS_DIR is not set. Build through Gradle (./gradlew buildNatives), "
        "which generates the JNI headers first.")
endif()

add_library(drawlist_accessor SHARED ImGuiDrawListExt.cpp)

target_include_directories(drawlist_accessor PRIVATE
    ${JNI_INCLUDE_DIRS}
    ${CMAKE_CURRENT_SOURCE_DIR}/imgui
    ${JNI_GENERATED_HEADERS_DIR}
)

if(WIN32)
    set_target_properties(drawlist_accessor PROPERTIES PREFIX "")   # MinGW would add "lib"
endif()

if(CMAKE_SYSTEM_NAME STREQUAL "Linux")
    target_link_options(drawlist_accessor PRIVATE
        -Wl,--no-undefined
        -static-libstdc++
        -static-libgcc
    )
endif()

set_target_properties(drawlist_accessor PROPERTIES
    LIBRARY_OUTPUT_DIRECTORY ${CMAKE_BINARY_DIR}/natives
    RUNTIME_OUTPUT_DIRECTORY ${CMAKE_BINARY_DIR}/natives
)