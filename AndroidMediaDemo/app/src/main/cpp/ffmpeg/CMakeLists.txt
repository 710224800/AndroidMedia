# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)

add_library(
             ffmpeg_lib
             SHARED
             ffmpeg_lib.cpp )

find_library(
              log-lib
              log )


set(distribution_DIR ${CMAKE_SOURCE_DIR}/src/main/jniLibs)
set(Android_ABI armeabi-v7a)

add_library( avutil-55
             SHARED
             IMPORTED )
set_target_properties( avutil-55
                       PROPERTIES IMPORTED_LOCATION
                       ${distribution_DIR}/${Android_ABI}/libavutil-55.so )

add_library( swresample-2
             SHARED
             IMPORTED )
set_target_properties( swresample-2
                       PROPERTIES IMPORTED_LOCATION
                       ${distribution_DIR}/${Android_ABI}/libswresample-2.so )
add_library( avcodec-57
             SHARED
             IMPORTED )
set_target_properties( avcodec-57
                       PROPERTIES IMPORTED_LOCATION
                       ${distribution_DIR}/${Android_ABI}/libavcodec-57.so )
add_library( avfilter-6
             SHARED
             IMPORTED)
set_target_properties( avfilter-6
                       PROPERTIES IMPORTED_LOCATION
                       ${distribution_DIR}/${Android_ABI}/libavfilter-6.so )
add_library( swscale-4
             SHARED
             IMPORTED)
set_target_properties( swscale-4
                       PROPERTIES IMPORTED_LOCATION
                       ${distribution_DIR}/${Android_ABI}/libswscale-4.so )
add_library( avdevice-57
             SHARED
             IMPORTED)
set_target_properties( avdevice-57
                       PROPERTIES IMPORTED_LOCATION
                       ${distribution_DIR}/${Android_ABI}/libavdevice-57.so )
add_library( avformat-57
             SHARED
             IMPORTED)
set_target_properties( avformat-57
                       PROPERTIES IMPORTED_LOCATION
                       ${distribution_DIR}/${Android_ABI}/libavformat-57.so )

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=gnu++11")



include_directories(${distribution_DIR}/include/)

#target_include_directories(ffmpeg_lib PRIVATE ${distribution_DIR}/include/)

target_link_libraries( ffmpeg_lib avcodec-57 avfilter-6 avformat-57 avutil-55 swresample-2 swscale-4 avdevice-57
                       ${log-lib} )