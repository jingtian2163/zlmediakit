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
include tests/CMakeFiles/test_httpApi.dir/depend.make
# Include any dependencies generated by the compiler for this target.
include tests/CMakeFiles/test_httpApi.dir/compiler_depend.make

# Include the progress variables for this target.
include tests/CMakeFiles/test_httpApi.dir/progress.make

# Include the compile flags for this target's objects.
include tests/CMakeFiles/test_httpApi.dir/flags.make

tests/CMakeFiles/test_httpApi.dir/test_httpApi.cpp.o: tests/CMakeFiles/test_httpApi.dir/flags.make
tests/CMakeFiles/test_httpApi.dir/test_httpApi.cpp.o: /Users/olaola/Desktop/ola/opensource/ZLMediaKit/tests/test_httpApi.cpp
tests/CMakeFiles/test_httpApi.dir/test_httpApi.cpp.o: tests/CMakeFiles/test_httpApi.dir/compiler_depend.ts
	@$(CMAKE_COMMAND) -E cmake_echo_color "--switch=$(COLOR)" --green --progress-dir=/Users/olaola/Desktop/ola/opensource/ZLMediaKit/build_android/CMakeFiles --progress-num=$(CMAKE_PROGRESS_1) "Building CXX object tests/CMakeFiles/test_httpApi.dir/test_httpApi.cpp.o"
	cd /Users/olaola/Desktop/ola/opensource/ZLMediaKit/build_android/tests && /Users/olaola/Desktop/ola/ndk/android-ndk-r20b/toolchains/llvm/prebuilt/darwin-x86_64/bin/clang++ --target=aarch64-none-linux-android21 --gcc-toolchain=/Users/olaola/Desktop/ola/ndk/android-ndk-r20b/toolchains/llvm/prebuilt/darwin-x86_64 --sysroot=/Users/olaola/Desktop/ola/ndk/android-ndk-r20b/toolchains/llvm/prebuilt/darwin-x86_64/sysroot $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -MD -MT tests/CMakeFiles/test_httpApi.dir/test_httpApi.cpp.o -MF CMakeFiles/test_httpApi.dir/test_httpApi.cpp.o.d -o CMakeFiles/test_httpApi.dir/test_httpApi.cpp.o -c /Users/olaola/Desktop/ola/opensource/ZLMediaKit/tests/test_httpApi.cpp

tests/CMakeFiles/test_httpApi.dir/test_httpApi.cpp.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color "--switch=$(COLOR)" --green "Preprocessing CXX source to CMakeFiles/test_httpApi.dir/test_httpApi.cpp.i"
	cd /Users/olaola/Desktop/ola/opensource/ZLMediaKit/build_android/tests && /Users/olaola/Desktop/ola/ndk/android-ndk-r20b/toolchains/llvm/prebuilt/darwin-x86_64/bin/clang++ --target=aarch64-none-linux-android21 --gcc-toolchain=/Users/olaola/Desktop/ola/ndk/android-ndk-r20b/toolchains/llvm/prebuilt/darwin-x86_64 --sysroot=/Users/olaola/Desktop/ola/ndk/android-ndk-r20b/toolchains/llvm/prebuilt/darwin-x86_64/sysroot $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -E /Users/olaola/Desktop/ola/opensource/ZLMediaKit/tests/test_httpApi.cpp > CMakeFiles/test_httpApi.dir/test_httpApi.cpp.i

tests/CMakeFiles/test_httpApi.dir/test_httpApi.cpp.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color "--switch=$(COLOR)" --green "Compiling CXX source to assembly CMakeFiles/test_httpApi.dir/test_httpApi.cpp.s"
	cd /Users/olaola/Desktop/ola/opensource/ZLMediaKit/build_android/tests && /Users/olaola/Desktop/ola/ndk/android-ndk-r20b/toolchains/llvm/prebuilt/darwin-x86_64/bin/clang++ --target=aarch64-none-linux-android21 --gcc-toolchain=/Users/olaola/Desktop/ola/ndk/android-ndk-r20b/toolchains/llvm/prebuilt/darwin-x86_64 --sysroot=/Users/olaola/Desktop/ola/ndk/android-ndk-r20b/toolchains/llvm/prebuilt/darwin-x86_64/sysroot $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -S /Users/olaola/Desktop/ola/opensource/ZLMediaKit/tests/test_httpApi.cpp -o CMakeFiles/test_httpApi.dir/test_httpApi.cpp.s

# Object files for target test_httpApi
test_httpApi_OBJECTS = \
"CMakeFiles/test_httpApi.dir/test_httpApi.cpp.o"

# External object files for target test_httpApi
test_httpApi_EXTERNAL_OBJECTS =

/Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/android/Release/test_httpApi: tests/CMakeFiles/test_httpApi.dir/test_httpApi.cpp.o
/Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/android/Release/test_httpApi: tests/CMakeFiles/test_httpApi.dir/build.make
/Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/android/Release/test_httpApi: /Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/android/Release/libjsoncpp.a
/Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/android/Release/test_httpApi: /Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/android/Release/libflv.a
/Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/android/Release/test_httpApi: /Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/android/Release/libmov.a
/Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/android/Release/test_httpApi: /Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/android/Release/libmpeg.a
/Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/android/Release/test_httpApi: /Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/android/Release/libzltoolkit.a
/Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/android/Release/test_httpApi: /Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/android/Release/libzlmediakit.a
/Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/android/Release/test_httpApi: /Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/android/Release/libext-codec.a
/Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/android/Release/test_httpApi: /Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/android/Release/libsrt.a
/Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/android/Release/test_httpApi: /Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/android/Release/libzlmediakit.a
/Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/android/Release/test_httpApi: /Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/android/Release/libjsoncpp.a
/Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/android/Release/test_httpApi: /Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/android/Release/libflv.a
/Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/android/Release/test_httpApi: /Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/android/Release/libmov.a
/Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/android/Release/test_httpApi: /Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/android/Release/libmpeg.a
/Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/android/Release/test_httpApi: /Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/android/Release/libzltoolkit.a
/Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/android/Release/test_httpApi: tests/CMakeFiles/test_httpApi.dir/link.txt
	@$(CMAKE_COMMAND) -E cmake_echo_color "--switch=$(COLOR)" --green --bold --progress-dir=/Users/olaola/Desktop/ola/opensource/ZLMediaKit/build_android/CMakeFiles --progress-num=$(CMAKE_PROGRESS_2) "Linking CXX executable /Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/android/Release/test_httpApi"
	cd /Users/olaola/Desktop/ola/opensource/ZLMediaKit/build_android/tests && $(CMAKE_COMMAND) -E cmake_link_script CMakeFiles/test_httpApi.dir/link.txt --verbose=$(VERBOSE)

# Rule to build all files generated by this target.
tests/CMakeFiles/test_httpApi.dir/build: /Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/android/Release/test_httpApi
.PHONY : tests/CMakeFiles/test_httpApi.dir/build

tests/CMakeFiles/test_httpApi.dir/clean:
	cd /Users/olaola/Desktop/ola/opensource/ZLMediaKit/build_android/tests && $(CMAKE_COMMAND) -P CMakeFiles/test_httpApi.dir/cmake_clean.cmake
.PHONY : tests/CMakeFiles/test_httpApi.dir/clean

tests/CMakeFiles/test_httpApi.dir/depend:
	cd /Users/olaola/Desktop/ola/opensource/ZLMediaKit/build_android && $(CMAKE_COMMAND) -E cmake_depends "Unix Makefiles" /Users/olaola/Desktop/ola/opensource/ZLMediaKit /Users/olaola/Desktop/ola/opensource/ZLMediaKit/tests /Users/olaola/Desktop/ola/opensource/ZLMediaKit/build_android /Users/olaola/Desktop/ola/opensource/ZLMediaKit/build_android/tests /Users/olaola/Desktop/ola/opensource/ZLMediaKit/build_android/tests/CMakeFiles/test_httpApi.dir/DependInfo.cmake "--color=$(COLOR)"
.PHONY : tests/CMakeFiles/test_httpApi.dir/depend

