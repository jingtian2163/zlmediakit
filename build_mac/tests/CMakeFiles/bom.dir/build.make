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
CMAKE_BINARY_DIR = /Users/olaola/Desktop/ola/opensource/ZLMediaKit/build_mac

# Include any dependencies generated for this target.
include tests/CMakeFiles/bom.dir/depend.make
# Include any dependencies generated by the compiler for this target.
include tests/CMakeFiles/bom.dir/compiler_depend.make

# Include the progress variables for this target.
include tests/CMakeFiles/bom.dir/progress.make

# Include the compile flags for this target's objects.
include tests/CMakeFiles/bom.dir/flags.make

tests/CMakeFiles/bom.dir/bom.cpp.o: tests/CMakeFiles/bom.dir/flags.make
tests/CMakeFiles/bom.dir/bom.cpp.o: /Users/olaola/Desktop/ola/opensource/ZLMediaKit/tests/bom.cpp
tests/CMakeFiles/bom.dir/bom.cpp.o: tests/CMakeFiles/bom.dir/compiler_depend.ts
	@$(CMAKE_COMMAND) -E cmake_echo_color "--switch=$(COLOR)" --green --progress-dir=/Users/olaola/Desktop/ola/opensource/ZLMediaKit/build_mac/CMakeFiles --progress-num=$(CMAKE_PROGRESS_1) "Building CXX object tests/CMakeFiles/bom.dir/bom.cpp.o"
	cd /Users/olaola/Desktop/ola/opensource/ZLMediaKit/build_mac/tests && /Applications/Xcode.app/Contents/Developer/Toolchains/XcodeDefault.xctoolchain/usr/bin/c++ $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -MD -MT tests/CMakeFiles/bom.dir/bom.cpp.o -MF CMakeFiles/bom.dir/bom.cpp.o.d -o CMakeFiles/bom.dir/bom.cpp.o -c /Users/olaola/Desktop/ola/opensource/ZLMediaKit/tests/bom.cpp

tests/CMakeFiles/bom.dir/bom.cpp.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color "--switch=$(COLOR)" --green "Preprocessing CXX source to CMakeFiles/bom.dir/bom.cpp.i"
	cd /Users/olaola/Desktop/ola/opensource/ZLMediaKit/build_mac/tests && /Applications/Xcode.app/Contents/Developer/Toolchains/XcodeDefault.xctoolchain/usr/bin/c++ $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -E /Users/olaola/Desktop/ola/opensource/ZLMediaKit/tests/bom.cpp > CMakeFiles/bom.dir/bom.cpp.i

tests/CMakeFiles/bom.dir/bom.cpp.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color "--switch=$(COLOR)" --green "Compiling CXX source to assembly CMakeFiles/bom.dir/bom.cpp.s"
	cd /Users/olaola/Desktop/ola/opensource/ZLMediaKit/build_mac/tests && /Applications/Xcode.app/Contents/Developer/Toolchains/XcodeDefault.xctoolchain/usr/bin/c++ $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -S /Users/olaola/Desktop/ola/opensource/ZLMediaKit/tests/bom.cpp -o CMakeFiles/bom.dir/bom.cpp.s

# Object files for target bom
bom_OBJECTS = \
"CMakeFiles/bom.dir/bom.cpp.o"

# External object files for target bom
bom_EXTERNAL_OBJECTS =

/Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/darwin/Debug/bom: tests/CMakeFiles/bom.dir/bom.cpp.o
/Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/darwin/Debug/bom: tests/CMakeFiles/bom.dir/build.make
/Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/darwin/Debug/bom: /usr/local/openssl/lib/libssl.dylib
/Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/darwin/Debug/bom: /usr/local/openssl/lib/libcrypto.dylib
/Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/darwin/Debug/bom: /Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/darwin/Debug/libjsoncpp.a
/Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/darwin/Debug/bom: /Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/darwin/Debug/libflv.a
/Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/darwin/Debug/bom: /Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/darwin/Debug/libmov.a
/Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/darwin/Debug/bom: /Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/darwin/Debug/libmpeg.a
/Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/darwin/Debug/bom: /Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/darwin/Debug/libzltoolkit.a
/Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/darwin/Debug/bom: /Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/darwin/Debug/libzlmediakit.a
/Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/darwin/Debug/bom: /Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/darwin/Debug/libext-codec.a
/Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/darwin/Debug/bom: /Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/darwin/Debug/libsrt.a
/Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/darwin/Debug/bom: /Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/darwin/Debug/libwebrtc.a
/Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/darwin/Debug/bom: /usr/local/lib/libsrtp2.a
/Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/darwin/Debug/bom: /Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/darwin/Debug/libzlmediakit.a
/Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/darwin/Debug/bom: /usr/local/openssl/lib/libssl.dylib
/Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/darwin/Debug/bom: /usr/local/openssl/lib/libcrypto.dylib
/Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/darwin/Debug/bom: /Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/darwin/Debug/libjsoncpp.a
/Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/darwin/Debug/bom: /Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/darwin/Debug/libflv.a
/Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/darwin/Debug/bom: /Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/darwin/Debug/libmov.a
/Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/darwin/Debug/bom: /Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/darwin/Debug/libmpeg.a
/Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/darwin/Debug/bom: /Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/darwin/Debug/libzltoolkit.a
/Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/darwin/Debug/bom: tests/CMakeFiles/bom.dir/link.txt
	@$(CMAKE_COMMAND) -E cmake_echo_color "--switch=$(COLOR)" --green --bold --progress-dir=/Users/olaola/Desktop/ola/opensource/ZLMediaKit/build_mac/CMakeFiles --progress-num=$(CMAKE_PROGRESS_2) "Linking CXX executable /Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/darwin/Debug/bom"
	cd /Users/olaola/Desktop/ola/opensource/ZLMediaKit/build_mac/tests && $(CMAKE_COMMAND) -E cmake_link_script CMakeFiles/bom.dir/link.txt --verbose=$(VERBOSE)

# Rule to build all files generated by this target.
tests/CMakeFiles/bom.dir/build: /Users/olaola/Desktop/ola/opensource/ZLMediaKit/release/darwin/Debug/bom
.PHONY : tests/CMakeFiles/bom.dir/build

tests/CMakeFiles/bom.dir/clean:
	cd /Users/olaola/Desktop/ola/opensource/ZLMediaKit/build_mac/tests && $(CMAKE_COMMAND) -P CMakeFiles/bom.dir/cmake_clean.cmake
.PHONY : tests/CMakeFiles/bom.dir/clean

tests/CMakeFiles/bom.dir/depend:
	cd /Users/olaola/Desktop/ola/opensource/ZLMediaKit/build_mac && $(CMAKE_COMMAND) -E cmake_depends "Unix Makefiles" /Users/olaola/Desktop/ola/opensource/ZLMediaKit /Users/olaola/Desktop/ola/opensource/ZLMediaKit/tests /Users/olaola/Desktop/ola/opensource/ZLMediaKit/build_mac /Users/olaola/Desktop/ola/opensource/ZLMediaKit/build_mac/tests /Users/olaola/Desktop/ola/opensource/ZLMediaKit/build_mac/tests/CMakeFiles/bom.dir/DependInfo.cmake "--color=$(COLOR)"
.PHONY : tests/CMakeFiles/bom.dir/depend

