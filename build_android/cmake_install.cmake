# Install script for directory: /Users/olaola/Desktop/ola/opensource/ZLMediaKit

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
    set(CMAKE_INSTALL_CONFIG_NAME "Release")
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

# Install shared libraries without execute permission?
if(NOT DEFINED CMAKE_INSTALL_SO_NO_EXE)
  set(CMAKE_INSTALL_SO_NO_EXE "0")
endif()

# Is this installation the result of a crosscompile?
if(NOT DEFINED CMAKE_CROSSCOMPILING)
  set(CMAKE_CROSSCOMPILING "TRUE")
endif()

# Set default install directory permissions.
if(NOT DEFINED CMAKE_OBJDUMP)
  set(CMAKE_OBJDUMP "/Users/olaola/Desktop/ola/ndk/android-ndk-r20b/toolchains/llvm/prebuilt/darwin-x86_64/bin/aarch64-linux-android-objdump")
endif()

if(NOT CMAKE_INSTALL_LOCAL_ONLY)
  # Include the install script for each subdirectory.
  include("/Users/olaola/Desktop/ola/opensource/ZLMediaKit/build_android/3rdpart/cmake_install.cmake")
  include("/Users/olaola/Desktop/ola/opensource/ZLMediaKit/build_android/src/cmake_install.cmake")
  include("/Users/olaola/Desktop/ola/opensource/ZLMediaKit/build_android/ext-codec/cmake_install.cmake")
  include("/Users/olaola/Desktop/ola/opensource/ZLMediaKit/build_android/srt/cmake_install.cmake")
  include("/Users/olaola/Desktop/ola/opensource/ZLMediaKit/build_android/api/cmake_install.cmake")
  include("/Users/olaola/Desktop/ola/opensource/ZLMediaKit/build_android/server/cmake_install.cmake")
  include("/Users/olaola/Desktop/ola/opensource/ZLMediaKit/build_android/tests/cmake_install.cmake")

endif()

if(CMAKE_INSTALL_COMPONENT)
  set(CMAKE_INSTALL_MANIFEST "install_manifest_${CMAKE_INSTALL_COMPONENT}.txt")
else()
  set(CMAKE_INSTALL_MANIFEST "install_manifest.txt")
endif()

string(REPLACE ";" "\n" CMAKE_INSTALL_MANIFEST_CONTENT
       "${CMAKE_INSTALL_MANIFEST_FILES}")
file(WRITE "/Users/olaola/Desktop/ola/opensource/ZLMediaKit/build_android/${CMAKE_INSTALL_MANIFEST}"
     "${CMAKE_INSTALL_MANIFEST_CONTENT}")
