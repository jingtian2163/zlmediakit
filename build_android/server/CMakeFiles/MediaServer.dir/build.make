# CMAKE generated file: DO NOT EDIT!
# Generated by "Unix Makefiles" Generator, CMake Version 3.28

# Delete rule output on recipe failure.
.DELETE_ON_ERROR:

#=============================================================================
# Special targets provided by cmake.

# Disable implicit rules so canonical targets will work.
.SUFFIXES:

# Disable VCS-based implicit rules.
% : %,v

# Disable VCS-based implicit rules.
% : RCS/%

# Disable VCS-based implicit rules.
% : RCS/%,v

# Disable VCS-based implicit rules.
% : SCCS/s.%

# Disable VCS-based implicit rules.
% : s.%

.SUFFIXES: .hpux_make_needs_suffix_list

# Produce verbose output by default.
VERBOSE = 1

# Command-line flag to silence nested $(MAKE).
$(VERBOSE)MAKESILENT = -s

#Suppress display of executed commands.
$(VERBOSE).SILENT:

# A target that is always out of date.
cmake_force:
.PHONY : cmake_force

#=============================================================================
# Set environment variables for the build.

# The shell in which to execute make rules.
SHELL = /bin/sh

# The CMake executable.
CMAKE_COMMAND = /opt/homebrew/Cellar/cmake/3.28.3/bin/cmake

# The command to remove a file.
RM = /opt/homebrew/Cellar/cmake/3.28.3/bin/cmake -E rm -f

# Escaping for special characters.
EQUALS = =

# The top-level source directory on which CMake was run.
CMAKE_SOURCE_DIR = /Users/olaola/Desktop/ola/opensource/ZLMediaKit

# The top-level build directory on which CMake was run.
CMAKE_BINARY_DIR = /Users/olaola/Desktop/ola/opensource/ZLMediaKit/build_android

# Include any dependencies generated for this target.
include server/CMakeFiles/MediaServer.dir/depend.make
# Include any dependencies generated by the compiler for this target.
include server/CMakeFiles/MediaServer.dir/compiler_depend.make

# Include the progress variables for this target.
include server/CMakeFiles/MediaServer.dir/progress.make

# Include the compile flags for this target's objects.
include server/CMakeFiles/MediaServer.dir/flags.make

server/CMakeFiles/MediaServer.dir/FFmpegSource.cpp.o: server/CMakeFiles/MediaServer.dir/flags.make
server/CMakeFiles/MediaServer.dir/FFmpegSource.cpp.o: /Users/olaola/Desktop/ola/opensource/ZLMediaKit/server/FFmpegSource.cpp
server/CMakeFiles/MediaServer.dir/FFmpegSource.cpp.o: server/CMakeFiles/MediaServer.dir/compiler_depend.ts
	@$(CMAKE_COMMAND) -E cmake_echo_color "--switch=$(COLOR)" --green --progress-dir=/Users/olaola/Desktop/ola/opensource/ZLMediaKit/build_android/CMakeFiles --progress-num=$(CMAKE_PROGRESS_1) "Building CXX object server/CMakeFiles/MediaServer.dir/FFmpegSource.cpp.o"
	cd /Users/olaola/Desktop/ola/opensource/ZLMediaKit/build_android/server && /Users/olaola/Desktop/ola/ndk/android-ndk-r20b/toolchains/llvm/prebuilt/darwin-x86_64/bin/clang++ --target=aarch64-none-linux-android21 --gcc-toolchain=/Users/olaola/Desktop/ola/ndk/android-ndk-r20b/toolchains/llvm/prebuilt/darwin-x86_64 --sysroot=/Users/olaola/Desktop/ola/ndk/android-ndk-r20b/toolchains/llvm/prebuilt/darwin-x86_64/sysroot $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -MD -MT server/CMakeFiles/MediaServer.dir/FFmpegSource.cpp.o -MF CMakeFiles/MediaServer.dir/FFmpegSource.cpp.o.d -o CMakeFiles/MediaServer.dir/FFmpegSource.cpp.o -c /Users/olaola/Desktop/ola/opensource/ZLMediaKit/server/FFmpegSource.cpp

server/CMakeFiles/MediaServer.dir/FFmpegSource.cpp.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color "--switch=$(COLOR)" --green "Preprocessing CXX source to CMakeFiles/MediaServer.dir/FFmpegSource.cpp.i"
	cd /Users/olaola/Desktop/ola/opensource/ZLMediaKit/build_android/server && /Users/olaola/Desktop/ola/ndk/android-ndk-r20b/toolchains/llvm/prebuilt/darwin-x86_64/bin/clang++ --target=aarch64-none-linux-android21 --gcc-toolchain=/Users/olaola/Desktop/ola/ndk/android-ndk-r20b/toolchains/llvm/prebuilt/darwin-x86_64 --sysroot=/Users/olaola/Desktop/ola/ndk/android-ndk-r20b/toolchains/llvm/prebuilt/darwin-x86_64/sysroot $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -E /Users/olaola/Desktop/ola/opensource/ZLMediaKit/server/FFmpegSource.cpp > CMakeFiles/MediaServer.dir/FFmpegSource.cpp.i

server/CMakeFiles/MediaServer.dir/FFmpegSource.cpp.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color "--switch=$(COLOR)" --green "Compiling CXX source to assembly CMakeFiles/MediaServer.dir/FFmpegSource.cpp.s"
	cd /Users/olaola/Desktop/ola/opensource/ZLMediaKit/build_android/server && /Users/olaola/Desktop/ola/ndk/android-ndk-r20b/toolchains/llvm/prebuilt/darwin-x86_64/bin/clang++ --target=aarch64-none-linux-android21 --gcc-toolchain=/Users/olaola/Desktop/ola/ndk/android-ndk-r20b/toolchains/llvm/prebuilt/darwin-x86_64 --sysroot=/Users/olaola/Desktop/ola/ndk/android-ndk-r20b/toolchains/llvm/prebuilt/darwin-x86_64/sysroot $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -S /Users/olaola/Desktop/ola/opensource/ZLMediaKit/server/FFmpegSource.cpp -o CMakeFiles/MediaServer.dir/FFmpegSource.cpp.s

server/CMakeFiles/MediaServer.dir/Process.cpp.o: server/CMakeFiles/MediaServer.dir/flags.make
server/CMakeFiles/MediaServer.dir/Process.cpp.o: /Users/olaola/Desktop/ola/opensource/ZLMediaKit/server/Process.cpp
server/CMakeFiles/MediaServer.dir/Process.cpp.o: server/CMakeFiles/MediaServer.dir/compiler_depend.ts
	@$(CMAKE_COMMAND) -E cmake_echo_color "--switch=$(COLOR)" --green --progress-dir=/Users/olaola/Desktop/ola/opensource/ZLMediaKit/build_android/CMakeFiles --progress-num=$(CMAKE_PROGRESS_2) "Building CXX object server/CMakeFiles/MediaServer.dir/Process.cpp.o"
	cd /Users/olaola/Desktop/ola/opensource/ZLMediaKit/build_android/server && /Users/olaola/Desktop/ola/ndk/android-ndk-r20b/toolchains/llvm/prebuilt/darwin-x86_64/bin/clang++ --target=aarch64-none-linux-android21 --gcc-toolchain=/Users/olaola/Desktop/ola/ndk/android-ndk-r20b/toolchains/llvm/prebuilt/darwin-x86_64 --sysroot=/Users/olaola/Desktop/ola/ndk/android-ndk-r20b/toolchains/llvm/prebuilt/darwin-x86_64/sysroot $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -MD -MT server/CMakeFiles/MediaServer.dir/Process.cpp.o -MF CMakeFiles/MediaServer.dir/Process.cpp.o.d -o CMakeFiles/MediaServer.dir/Process.cpp.o -c /Users/olaola/Desktop/ola/opensource/ZLMediaKit/server/Process.cpp

server/CMakeFiles/MediaServer.dir/Process.cpp.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color "--switch=$(COLOR)" --green "Preprocessing CXX source to CMakeFiles/MediaServer.dir/Process.cpp.i"
	cd /Users/olaola/Desktop/ola/opensource/ZLMediaKit/build_android/server && /Users/olaola/Desktop/ola/ndk/android-ndk-r20b/toolchains/llvm/prebuilt/darwin-x86_64/bin/clang++ --target=aarch64-none-linux-android21 --gcc-toolchain=/Users/olaola/Desktop/ola/ndk/android-ndk-r20b/toolchains/llvm/prebuilt/darwin-x86_64 --sysroot=/Users/olaola/Desktop/ola/ndk/android-ndk-r20b/toolchains/llvm/prebuilt/darwin-x86_64/sysroot $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -E /Users/olaola/Desktop/ola/opensource/ZLMediaKit/server/Process.cpp > CMakeFiles/MediaServer.dir/Process.cpp.i

server/CMakeFiles/MediaServer.dir/Process.cpp.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color "--switch=$(COLOR)" --green "Compiling CXX source to assembly CMakeFiles/MediaServer.dir/Process.cpp.s"
	cd /Users/olaola/Desktop/ola/opensource/ZLMediaKit/build_android/server && /Users/olaola/Desktop/ola/ndk/android-ndk-r20b/toolchains/llvm/prebuilt/darwin-x86_64/bin/clang++ --target=aarch64-none-linux-android21 --gcc-toolchain=/Users/olaola/Desktop/ola/ndk/android-ndk-r20b/toolchains/llvm/prebuilt/darwin-x86_64 --sysroot=/Users/olaola/Desktop/ola/ndk/android-ndk-r20b/toolchains/llvm/prebuilt/darwin-x86_64/sysroot $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -S /Users/olaola/Desktop/ola/opensource/ZLMediaKit/server/Process.cpp -o CMakeFiles/MediaServer.dir/Process.cpp.s

server/CMakeFiles/MediaServer.dir/System.cpp.o: server/CMakeFiles/MediaServer.dir/flags.make
server/CMakeFiles/MediaServer.dir/System.cpp.o: /Users/olaola/Desktop/ola/opensource/ZLMediaKit/server/System.cpp
server/CMakeFiles/MediaServer.dir/System.cpp.o: server/CMakeFiles/MediaServer.dir/compiler_depend.ts
	@$(CMAKE_COMMAND) -E cmake_echo_color "--switch=$(COLOR)" --green --progress-dir=/Users/olaola/Desktop/ola/opensource/ZLMediaKit/build_android/CMakeFiles --progress-num=$(CMAKE_PROGRESS_3) "Building CXX object server/CMakeFiles/MediaServer.dir/System.cpp.o"
	cd /Users/olaola/Desktop/ola/opensource/ZLMediaKit/build_android/server && /Users/olaola/Desktop/ola/ndk/android-ndk-r20b/toolchains/llvm/prebuilt/darwin-x86_64/bin/clang++ --target=aarch64-none-linux-android21 --gcc-toolchain=/Users/olaola/Desktop/ola/ndk/android-ndk-r20b/toolchains/llvm/prebuilt/darwin-x86_64 --sysroot=/Users/olaola/Desktop/ola/ndk/android-ndk-r20b/toolchains/llvm/prebuilt/darwin-x86_64/sysroot $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -MD -MT server/CMakeFiles/MediaServer.dir/System.cpp.o -MF CMakeFiles/MediaServer.dir/System.cpp.o.d -o CMakeFiles/MediaServer.dir/System.cpp.o -c /Users/olaola/Desktop/ola/opensource/ZLMediaKit/server/System.cpp

server/CMakeFiles/MediaServer.dir/System.cpp.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color "--switch=$(COLOR)" --green "Preprocessing CXX source to CMakeFiles/MediaServer.dir/System.cpp.i"
	cd /Users/olaola/Desktop/ola/opensource/ZLMediaKit/build_android/server && /Users/olaola/Desktop/ola/ndk/android-ndk-r20b/toolchains/llvm/prebuilt/darwin-x86_64/bin/clang++ --target=aarch64-none-linux-android21 --gcc-toolchain=/Users/olaola/Desktop/ola/ndk/android-ndk-r20b/toolchains/llvm/prebuilt/darwin-x86_64 --sysroot=/Users/olaola/Desktop/ola/ndk/android-ndk-r20b/toolchains/llvm/prebuilt/darwin-x86_64/sysroot $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -E /Users/olaola/Desktop/ola/opensource/ZLMediaKit/server/System.cpp > CMakeFiles/MediaServer.dir/System.cpp.i

server/CMakeFiles/MediaServer.dir/System.cpp.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color "--switch=$(COLOR)" --green "Compiling CXX source to assembly CMakeFiles/MediaServer.dir/System.cpp.s"
	cd /Users/olaola/Desktop/ola/opensource/ZLMediaKit/build_android/server && /Users/olaola/Desktop/ola/ndk/android-ndk-r20b/toolchains/llvm/prebuilt/darwin-x86_64/bin/clang++ --target=aarch64-none-linux-android21 --gcc-toolchain=/Users/olaola/Desktop/ola/ndk/android-ndk-r20b/toolchains/llvm/prebuilt/darwin-x86_64 --sysroot=/Users/olaola/Desktop/ola/ndk/android-ndk-r20b/toolchains/llvm/prebuilt/darwin-x86_64/sysroot $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -S /Users/olaola/Desktop/ola/opensource/ZLMediaKit/server/System.cpp -o CMakeFiles/MediaServer.dir/System.cpp.s

server/CMakeFiles/MediaServer.dir/VideoStack.cpp.o: server/CMakeFiles/MediaServer.dir/flags.make
server/CMakeFiles/MediaServer.dir/VideoStack.cpp.o: /Users/olaola/Desktop/ola/opensource/ZLMediaKit/server/VideoStack.cpp
server/CMakeFiles/MediaServer.dir/VideoStack.cpp.o: server/CMakeFiles/MediaServer.dir/compiler_depend.ts
	@$(CMAKE_COMMAND) -E cmake_echo_color "--switch=$(COLOR)" --green --progress-dir=/Users/olaola/Desktop/ola/opensource/ZLMediaKit/build_android/CMakeFiles --progress-num=$(CMAKE_PROGRESS_4) "Building CXX object server/CMakeFiles/MediaServer.dir/VideoStack.cpp.o"
	cd /Users/olaola/Desktop/ola/opensource/ZLMediaKit/build_android/server && /Users/olaola/Desktop/ola/ndk/android-ndk-r20b/toolchains/llvm/prebuilt/darwin-x86_64/bin/clang++ --target=aarch64-none-linux-android21 --gcc-toolchain=/Users/olaola/Desktop/ola/ndk/android-ndk-r20b/toolchains/llvm/prebuilt/darwin-x86_64 --sysroot=/Users/olaola/Desktop/ola/ndk/android-ndk-r20b/toolchains/llvm/prebuilt/darwin-x86_64/sysroot $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -MD -MT server/CMakeFiles/MediaServer.dir/VideoStack.cpp.o -MF CMakeFiles/MediaServer.dir/VideoStack.cpp.o.d -o CMakeFiles/MediaServer.dir/VideoStack.cpp.o -c /Users/olaola/Desktop/ola/opensource/ZLMediaKit/server/VideoStack.cpp

server/CMakeFiles/MediaServer.dir/VideoStack.cpp.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color "--switch=$(COLOR)" --green "Preprocessing CXX source to CMakeFiles/MediaServer.dir/VideoStack.cpp.i"
	cd /Users/olaola/Desktop/ola/opensource/ZLMediaKit/build_android/server && /Users/olaola/Desktop/ola/ndk/android-ndk-r20b/toolchains/llvm/prebuilt/darwin-x86_64/bin/clang++ --target=aarch64-none-linux-android21 --gcc-toolchain=/Users/olaola/Desktop/ola/ndk/android-ndk-r20b/toolchains/llvm/prebuilt/darwin-x86_64 --sysroot=/Users/olaola/Desktop/ola/ndk/android-ndk-r20b/toolchains/llvm/prebuilt/darwin-x86_64/sysroot $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -E /Users/olaola/Desktop/ola/opensource/ZLMediaKit/server/VideoStack.cpp > CMakeFiles/MediaServer.dir/VideoStack.cpp.i

server/CMakeFiles/MediaServer.dir/VideoStack.cpp.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color "--switch=$(COLOR)" --green "Compiling CXX source to assembly CMakeFiles/MediaServer.dir/VideoStack.cpp.s"
	cd /Users/olaola/Desktop/ola/opensource/ZLMediaKit/build_android/server && /Users/olaola/Desktop/ola/ndk/android-ndk-r20b/toolchains/llvm/prebuilt/darwin-x86_64/bin/clang++ --target=aarch64-none-linux-android21 --gcc-toolchain=/Users/olaola/Desktop/ola/ndk/android-ndk-r20b/toolchains/llvm/prebuilt/darwin-x86_64 --sysroot=/Users/olaola/Desktop/ola/ndk/android-ndk-r20b/toolchains/llvm/prebuilt/darwin-x86_64/sysroot $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -S /Users/olaola/Desktop/ola/opensource/ZLMediaKit/server/VideoStack.cpp -o CMakeFiles/MediaServer.dir/VideoStack.cpp.s

server/CMakeFiles/MediaServer.dir/WebApi.cpp.o: server/CMakeFiles/MediaServer.dir/flags.make
server/CMakeFiles/MediaServer.dir/WebApi.cpp.o: /Users/olaola/Desktop/ola/opensource/ZLMediaKit/server/WebApi.cpp
server/CMakeFiles/MediaServer.dir/WebApi.cpp.o: server/CMakeFiles/MediaServer.dir/compiler_depend.ts
	@$(CMAKE_COMMAND) -E cmake_echo_color "--switch=$(COLOR)" --green --progress-dir=/Users/olaola/Desktop/ola/opensource/ZLMediaKit/build_android/CMakeFiles --progress-num=$(CMAKE_PROGRESS_5) "Building CXX object server/CMakeFiles/MediaServer.dir/WebApi.cpp.o"
	cd /Users/olaola/Desktop/ola/opensource/ZLMediaKit/build_android/server && /Users/olaola/Desktop/ola/ndk/android-ndk-r20b/toolchains/llvm/prebuilt/darwin-x86_64/bin/clang++ --target=aarch64-none-linux-android21 --gcc-toolchain=/Users/olaola/Desktop/ola/ndk/android-ndk-r20b/toolchains/llvm/prebuilt/darwin-x86_64 --sysroot=/Users/olaola/Desktop/ola/ndk/android-ndk-r20b/toolchains/llvm/prebuilt/darwin-x86_64/sysroot $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -MD -MT server/CMakeFiles/MediaServer.dir/WebApi.cpp.o -MF CMakeFiles/MediaServer.dir/WebApi.cpp.o.d -o CMakeFiles/MediaServer.dir/WebApi.cpp.o -c /Users/olaola/Desktop/ola/opensource/ZLMediaKit/server/WebApi.cpp

server/CMakeFiles/MediaServer.dir/WebApi.cpp.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color "--switch=$(COLOR)" --green "Preprocessing CXX source to CMakeFiles/MediaServer.dir/WebApi.cpp.i"
	cd /Users/olaola/Desktop/ola/opensource/ZLMediaKit/build_android/server && /Users/olaola/Desktop/ola/ndk/android-ndk-r20b/toolchains/llvm/prebuilt/darwin-x86_64/bin/clang++ --target=aarch64-none-linux-android21 --gcc-toolchain=/Users/olaola/Desktop/ola/ndk/android-ndk-r20b/toolchains/llvm/prebuilt/darwin-x86_64 --sysroot=/Users/olaola/Desktop/ola/ndk/android-ndk-r20b/toolchains/llvm/prebuilt/darwin-x86_64/sysroot $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -E /Users/olaola/Desktop/ola/opensource/ZLMediaKit/server/WebApi.cpp > CMakeFiles/MediaServer.dir/WebApi.cpp.i

server/CMakeFiles/MediaServer.dir/WebApi.cpp.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color "--switch=$(COLOR)" --green "Compiling CXX source to assembly CMakeFiles/MediaServer.dir/WebApi.cpp.s"
	cd /Users/olaola/Desktop/ola/opensource/ZLMediaKit/build_android/server && /Users/olaola/Desktop/ola/ndk/android-ndk-r20b/toolchains/llvm/prebuilt/darwin-x86_64/bin/clang++ --target=aarch64-none-linux-android21 --gcc-toolchain=/Users/olaola/Desktop/ola/ndk/android-ndk-r20b/toolchains/llvm/prebuilt/darwin-x86_64 --sysroot=/Users/olaola/Desktop/ola/ndk/android-ndk-r20b/toolchains/llvm/prebuilt/darwin-x86_64/sysroot $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -S /Users/olaola/Desktop/ola/opensource/ZLMediaKit/server/WebApi.cpp -o CMakeFiles/MediaServer.dir/WebApi.cpp.s

server/CMakeFiles/MediaServer.dir/WebHook.cpp.o: server/CMakeFiles/MediaServer.dir/flags.make
server/CMakeFiles/MediaServer.dir/WebHook.cpp.o: /Users/olaola/Desktop/ola/opensource/ZLMediaKit/server/WebHook.cpp
server/CMakeFiles/MediaServer.dir/WebHook.cpp.o: server/CMakeFiles/MediaServer.dir/compiler_depend.ts
	@$(CMAKE_COMMAND) -E cmake_echo_color "--switch=$(COLOR)" --green --progress-dir=/Users/olaola/Desktop/ola/opensource/ZLMediaKit/build_android/CMakeFiles --progress-num=$(CMAKE_PROGRESS_6) "Building CXX object server/CMakeFiles/MediaServer.dir/WebHook.cpp.o"
	cd /Users/olaola/Desktop/ola/opensource/ZLMediaKit/build_android/server && /Users/olaola/Desktop/ola/ndk/android-ndk-r20b/toolchains/llvm/prebuilt/darwin-x86_64/bin/clang++ --target=aarch64-none-linux-android21 --gcc-toolchain=/Users/olaola/Desktop/ola/ndk/android-ndk-r20b/toolchains/llvm/prebuilt/darwin-x86_64 --sysroot=/Users/olaola/Desktop/ola/ndk/android-ndk-r20b/toolchains/llvm/prebuilt/darwin-x86_64/sysroot $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -MD -MT server/CMakeFiles/MediaServer.dir/WebHook.cpp.o -MF CMakeFiles/MediaServer.dir/WebHook.cpp.o.d -o CMakeFiles/MediaServer.dir/WebHook.cpp.o -c /Users/olaola/Desktop/ola/opensource/ZLMediaKit/server/WebHook.cpp

server/CMakeFiles/MediaServer.dir/WebHook.cpp.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color "--switch=$(COLOR)" --green "Preprocessing CXX source to CMakeFiles/MediaServer.dir/WebHook.cpp.i"
	cd /Users/olaola/Desktop/ola/opensource/ZLMediaKit/build_android/server && /Users/olaola/Desktop/ola/ndk/android-ndk-r20b/toolchains/llvm/prebuilt/darwin-x86_64/bin/clang++ --target=aarch64-none-linux-android21 --gcc-toolchain=/Users/olaola/Desktop/ola/ndk/android-ndk-r20b/toolchains/llvm/prebuilt/darwin-x86_64 --sysroot=/Users/olaola/Desktop/ola/ndk/android-ndk-r20b/toolchains/llvm/prebuilt/darwin-x86_64/sysroot $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -E /Users/olaola/Desktop/ola/opensource/ZLMediaKit/server/WebHook.cpp > CMakeFiles/MediaServer.dir/WebHook.cpp.i

server/CMakeFiles/MediaServer.dir/WebHook.cpp.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color "--switch=$(COLOR)" --green "Compiling CXX source to assembly CMakeFiles/MediaServer.dir/WebHook.cpp.s"
	cd /Users/olaola/Desktop/ola/opensource/ZLMediaKit/build_android/server && /Users/olaola/Desktop/ola/ndk/android-ndk-r20b/toolchains/llvm/prebuilt/darwin-x86_64/bin/clang++ --target=aarch64-none-linux-android21 --gcc-toolchain=/Users/olaola/Desktop/ola/ndk/android-ndk-r20b/toolchains/llvm/prebuilt/darwin-x86_64 --sysroot=/Users/olaola/Desktop/ola/ndk/android-ndk-r20b/toolchains/llvm/prebuilt/darwin-x86_64/sysroot $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -S /Users/olaola/Desktop/ola/opensource/ZLMediaKit/server/WebHook.cpp -o CMakeFiles/MediaServer.dir/WebHook.cpp.s

server/CMakeFiles/MediaServer.dir/main.cpp.o: server/CMakeFiles/MediaServer.dir/flags.make
server/CMakeFiles/MediaServer.dir/main.cpp.o: /Users/olaola/Desktop/ola/opensource/ZLMediaKit/server/main.cpp
server/CMakeFiles/MediaServer.dir/main.cpp.o: server/CMakeFiles/MediaServer.dir/compiler_depend.ts
	@$(CMAKE_COMMAND) -E cmake_echo_color "--switch=$(COLOR)" --green --progress-dir=/Users/olaola/Desktop/ola/opensource/ZLMediaKit/build_android/CMakeFiles --progress-num=$(CMAKE_PROGRESS_7) "Building CXX object server/CMakeFiles/MediaServer.dir/main.cpp.o"
	cd /Users/olaola/Desktop/ola/opensource/ZLMediaKit/build_android/server && /Users/olaola/Desktop/ola/ndk/android-ndk-r20b/toolchains/llvm/prebuilt/darwin-x86_64/bin/clang++ --target=aarch64-none-linux-android21 --gcc-toolchain=/Users/olaola/Desktop/ola/ndk/android-ndk-r20b/toolchains/llvm/prebuilt/darwin-x86_64 --sysroot=/Users/olaola/Desktop/ola/ndk/android-ndk-r20b/toolchains/llvm/prebuilt/darwin-x86_64/sysroot $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -MD -MT server/CMakeFiles/MediaServer.dir/main.cpp.o -MF CMakeFiles/MediaServer.dir/main.cpp.o.d -o CMakeFiles/MediaServer.dir/main.cpp.o -c /Users/olaola/Desktop/ola/opensource/ZLMediaKit/server/main.cpp

server/CMakeFiles/MediaServer.dir/main.cpp.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color "--switch=$(COLOR)" --green "Preprocessing CXX source to CMakeFiles/MediaServer.dir/main.cpp.i"
	cd /Users/olaola/Desktop/ola/opensource/ZLMediaKit/build_android/server && /Users/olaola/Desktop/ola/ndk/android-ndk-r20b/toolchains/llvm/prebuilt/darwin-x86_64/bin/clang++ --target=aarch64-none-linux-android21 --gcc-toolchain=/Users/olaola/Desktop/ola/ndk/android-ndk-r20b/toolchains/llvm/prebuilt/darwin-x86_64 --sysroot=/Users/olaola/Desktop/ola/ndk/android-ndk-r20b/toolchains/llvm/prebuilt/darwin-x86_64/sysroot $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -E /Users/olaola/Desktop/ola/opensource/ZLMediaKit/server/main.cpp > CMakeFiles/MediaServer.dir/main.cpp.i

server/CMakeFiles/MediaServer.dir/main.cpp.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color "--switch=$(COLOR)" --green "Compiling CXX source to assembly CMakeFiles/MediaServer.dir/main.cpp.s"
	cd /Users/olaola/Desktop/ola/opensource/ZLMediaKit/build_android/server && /Users/olaola/Desktop/ola/ndk/android-ndk-r20b/toolchains/llvm/prebuilt/darwin-x86_64/bin/clang++ --target=aarch64-none-linux-android21 --gcc-toolchain=/Users/olaola/Desktop/ola/ndk/android-ndk-r20b/toolchains/llvm/prebuilt/darwin-x86_64 --sysroot=/Users/olaola/Desktop/ola/ndk/android-ndk-r20b/toolchains/llvm/prebuilt/darwin-x86_64/sysroot $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -S /Users/olaola/Desktop/ola/opensource/ZLMediaKit/server/main.cpp -o CMakeFiles/MediaServer.dir/main.cpp.s

# Object files for target MediaServer
MediaServer_OBJECTS = \
"CMakeFiles/MediaServer.dir/FFmpegSource.cpp.o" \
"CMakeFiles/MediaServer.dir/Process.cpp.o" \
"CMakeFiles/MediaServer.dir/System.cpp.o" \
"CMakeFiles/MediaServer.dir/VideoStack.cpp.o" \
"CMakeFiles/MediaServer.dir/WebApi.cpp.o" \
"CMakeFiles/MediaServer.dir/WebHook.cpp.o" \
"CMakeFiles/MediaServer.dir/main.cpp.o"

# External object files for target MediaServer
MediaServer_EXTERNAL_OBJECTS =

/Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/android/Release/MediaServer: server/CMakeFiles/MediaServer.dir/FFmpegSource.cpp.o
/Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/android/Release/MediaServer: server/CMakeFiles/MediaServer.dir/Process.cpp.o
/Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/android/Release/MediaServer: server/CMakeFiles/MediaServer.dir/System.cpp.o
/Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/android/Release/MediaServer: server/CMakeFiles/MediaServer.dir/VideoStack.cpp.o
/Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/android/Release/MediaServer: server/CMakeFiles/MediaServer.dir/WebApi.cpp.o
/Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/android/Release/MediaServer: server/CMakeFiles/MediaServer.dir/WebHook.cpp.o
/Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/android/Release/MediaServer: server/CMakeFiles/MediaServer.dir/main.cpp.o
/Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/android/Release/MediaServer: server/CMakeFiles/MediaServer.dir/build.make
/Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/android/Release/MediaServer: /Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/android/Release/libjsoncpp.a
/Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/android/Release/MediaServer: /Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/android/Release/libflv.a
/Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/android/Release/MediaServer: /Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/android/Release/libmov.a
/Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/android/Release/MediaServer: /Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/android/Release/libmpeg.a
/Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/android/Release/MediaServer: /Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/android/Release/libzltoolkit.a
/Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/android/Release/MediaServer: /Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/android/Release/libzlmediakit.a
/Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/android/Release/MediaServer: /Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/android/Release/libext-codec.a
/Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/android/Release/MediaServer: /Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/android/Release/libsrt.a
/Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/android/Release/MediaServer: /Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/android/Release/libzlmediakit.a
/Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/android/Release/MediaServer: /Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/android/Release/libjsoncpp.a
/Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/android/Release/MediaServer: /Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/android/Release/libflv.a
/Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/android/Release/MediaServer: /Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/android/Release/libmov.a
/Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/android/Release/MediaServer: /Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/android/Release/libmpeg.a
/Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/android/Release/MediaServer: /Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/android/Release/libzltoolkit.a
/Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/android/Release/MediaServer: server/CMakeFiles/MediaServer.dir/link.txt
	@$(CMAKE_COMMAND) -E cmake_echo_color "--switch=$(COLOR)" --green --bold --progress-dir=/Users/olaola/Desktop/ola/opensource/ZLMediaKit/build_android/CMakeFiles --progress-num=$(CMAKE_PROGRESS_8) "Linking CXX executable /Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/android/Release/MediaServer"
	cd /Users/olaola/Desktop/ola/opensource/ZLMediaKit/build_android/server && $(CMAKE_COMMAND) -E cmake_link_script CMakeFiles/MediaServer.dir/link.txt --verbose=$(VERBOSE)

# Rule to build all files generated by this target.
server/CMakeFiles/MediaServer.dir/build: /Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/android/Release/MediaServer
.PHONY : server/CMakeFiles/MediaServer.dir/build

server/CMakeFiles/MediaServer.dir/clean:
	cd /Users/olaola/Desktop/ola/opensource/ZLMediaKit/build_android/server && $(CMAKE_COMMAND) -P CMakeFiles/MediaServer.dir/cmake_clean.cmake
.PHONY : server/CMakeFiles/MediaServer.dir/clean

server/CMakeFiles/MediaServer.dir/depend:
	cd /Users/olaola/Desktop/ola/opensource/ZLMediaKit/build_android && $(CMAKE_COMMAND) -E cmake_depends "Unix Makefiles" /Users/olaola/Desktop/ola/opensource/ZLMediaKit /Users/olaola/Desktop/ola/opensource/ZLMediaKit/server /Users/olaola/Desktop/ola/opensource/ZLMediaKit/build_android /Users/olaola/Desktop/ola/opensource/ZLMediaKit/build_android/server /Users/olaola/Desktop/ola/opensource/ZLMediaKit/build_android/server/CMakeFiles/MediaServer.dir/DependInfo.cmake "--color=$(COLOR)"
.PHONY : server/CMakeFiles/MediaServer.dir/depend

