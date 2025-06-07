# Install script for directory: /Users/olaola/Desktop/ola/opensource/ZLMediaKit/api

# Set the install prefix
if(NOT DEFINED CMAKE_INSTALL_PREFIX)
  set(CMAKE_INSTALL_PREFIX "/usr/local")
endif()
string(REGEX REPLACE "/$" "" CMAKE_INSTALL_PREFIX "${CMAKE_INSTALL_PREFIX}")

# Set the install configuration name.
if(NOT DEFINED CMAKE_INSTALL_CONFIG_NAME)
  if(BUILD_TYPE)
    string(REGEX REPLACE "^[^A-Za-z0-9_]+" ""
           CMAKE_INSTALL_CONFIG_NAME "${BUILD_TYPE}")
  else()
    set(CMAKE_INSTALL_CONFIG_NAME "Debug")
  endif()
  message(STATUS "Install configuration: \"${CMAKE_INSTALL_CONFIG_NAME}\"")
endif()

# Set the component getting installed.
if(NOT CMAKE_INSTALL_COMPONENT)
  if(COMPONENT)
    message(STATUS "Install component: \"${COMPONENT}\"")
    set(CMAKE_INSTALL_COMPONENT "${COMPONENT}")
  else()
    set(CMAKE_INSTALL_COMPONENT)
  endif()
endif()

# Is this installation the result of a crosscompile?
if(NOT DEFINED CMAKE_CROSSCOMPILING)
  set(CMAKE_CROSSCOMPILING "FALSE")
endif()

# Set default install directory permissions.
if(NOT DEFINED CMAKE_OBJDUMP)
  set(CMAKE_OBJDUMP "/Applications/Xcode.app/Contents/Developer/Toolchains/XcodeDefault.xctoolchain/usr/bin/objdump")
endif()

if(CMAKE_INSTALL_COMPONENT STREQUAL "Unspecified" OR NOT CMAKE_INSTALL_COMPONENT)
  list(APPEND CMAKE_ABSOLUTE_DESTINATION_FILES
   "/usr/local/include/mk_common.h;/usr/local/include/mk_events.h;/usr/local/include/mk_events_objects.h;/usr/local/include/mk_frame.h;/usr/local/include/mk_h264_splitter.h;/usr/local/include/mk_httpclient.h;/usr/local/include/mk_media.h;/usr/local/include/mk_mediakit.h;/usr/local/include/mk_player.h;/usr/local/include/mk_proxyplayer.h;/usr/local/include/mk_pusher.h;/usr/local/include/mk_recorder.h;/usr/local/include/mk_rtp_server.h;/usr/local/include/mk_tcp.h;/usr/local/include/mk_thread.h;/usr/local/include/mk_track.h;/usr/local/include/mk_transcode.h;/usr/local/include/mk_util.h;/usr/local/include/mk_export.h")
  if(CMAKE_WARN_ON_ABSOLUTE_INSTALL_DESTINATION)
    message(WARNING "ABSOLUTE path INSTALL DESTINATION : ${CMAKE_ABSOLUTE_DESTINATION_FILES}")
  endif()
  if(CMAKE_ERROR_ON_ABSOLUTE_INSTALL_DESTINATION)
    message(FATAL_ERROR "ABSOLUTE path INSTALL DESTINATION forbidden (by caller): ${CMAKE_ABSOLUTE_DESTINATION_FILES}")
  endif()
  file(INSTALL DESTINATION "/usr/local/include" TYPE FILE FILES
    "/Users/olaola/Desktop/ola/opensource/ZLMediaKit/api/include/mk_common.h"
    "/Users/olaola/Desktop/ola/opensource/ZLMediaKit/api/include/mk_events.h"
    "/Users/olaola/Desktop/ola/opensource/ZLMediaKit/api/include/mk_events_objects.h"
    "/Users/olaola/Desktop/ola/opensource/ZLMediaKit/api/include/mk_frame.h"
    "/Users/olaola/Desktop/ola/opensource/ZLMediaKit/api/include/mk_h264_splitter.h"
    "/Users/olaola/Desktop/ola/opensource/ZLMediaKit/api/include/mk_httpclient.h"
    "/Users/olaola/Desktop/ola/opensource/ZLMediaKit/api/include/mk_media.h"
    "/Users/olaola/Desktop/ola/opensource/ZLMediaKit/api/include/mk_mediakit.h"
    "/Users/olaola/Desktop/ola/opensource/ZLMediaKit/api/include/mk_player.h"
    "/Users/olaola/Desktop/ola/opensource/ZLMediaKit/api/include/mk_proxyplayer.h"
    "/Users/olaola/Desktop/ola/opensource/ZLMediaKit/api/include/mk_pusher.h"
    "/Users/olaola/Desktop/ola/opensource/ZLMediaKit/api/include/mk_recorder.h"
    "/Users/olaola/Desktop/ola/opensource/ZLMediaKit/api/include/mk_rtp_server.h"
    "/Users/olaola/Desktop/ola/opensource/ZLMediaKit/api/include/mk_tcp.h"
    "/Users/olaola/Desktop/ola/opensource/ZLMediaKit/api/include/mk_thread.h"
    "/Users/olaola/Desktop/ola/opensource/ZLMediaKit/api/include/mk_track.h"
    "/Users/olaola/Desktop/ola/opensource/ZLMediaKit/api/include/mk_transcode.h"
    "/Users/olaola/Desktop/ola/opensource/ZLMediaKit/api/include/mk_util.h"
    "/Users/olaola/Desktop/ola/opensource/ZLMediaKit/build_mac/api/mk_export.h"
    )
endif()

if(CMAKE_INSTALL_COMPONENT STREQUAL "Unspecified" OR NOT CMAKE_INSTALL_COMPONENT)
  list(APPEND CMAKE_ABSOLUTE_DESTINATION_FILES
   "/usr/local/lib/libmk_api.dylib")
  if(CMAKE_WARN_ON_ABSOLUTE_INSTALL_DESTINATION)
    message(WARNING "ABSOLUTE path INSTALL DESTINATION : ${CMAKE_ABSOLUTE_DESTINATION_FILES}")
  endif()
  if(CMAKE_ERROR_ON_ABSOLUTE_INSTALL_DESTINATION)
    message(FATAL_ERROR "ABSOLUTE path INSTALL DESTINATION forbidden (by caller): ${CMAKE_ABSOLUTE_DESTINATION_FILES}")
  endif()
  file(INSTALL DESTINATION "/usr/local/lib" TYPE SHARED_LIBRARY FILES "/Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/darwin/Debug/libmk_api.dylib")
  if(EXISTS "$ENV{DESTDIR}/usr/local/lib/libmk_api.dylib" AND
     NOT IS_SYMLINK "$ENV{DESTDIR}/usr/local/lib/libmk_api.dylib")
    if(CMAKE_INSTALL_DO_STRIP)
      execute_process(COMMAND "/Applications/Xcode.app/Contents/Developer/Toolchains/XcodeDefault.xctoolchain/usr/bin/strip" -x "$ENV{DESTDIR}/usr/local/lib/libmk_api.dylib")
    endif()
  endif()
endif()

if(CMAKE_INSTALL_COMPONENT STREQUAL "Unspecified" OR NOT CMAKE_INSTALL_COMPONENT)
endif()

if(NOT CMAKE_INSTALL_LOCAL_ONLY)
  # Include the install script for each subdirectory.
  include("/Users/olaola/Desktop/ola/opensource/ZLMediaKit/build_mac/api/tests/cmake_install.cmake")

endif()

