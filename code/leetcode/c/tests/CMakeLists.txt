aux_source_directory(. C_TEST_SOURCES)
foreach (testsourcefile ${C_TEST_SOURCES})
    get_filename_component(testname ${testsourcefile} NAME_WE)
    add_executable(${testname} ${testsourcefile})
    target_link_libraries(${testname} lc)
    target_link_libraries(${testname} gtest_main)
    add_test(NAME ${testname} COMMAND ${testname})
endforeach (testsourcefile ${C_TEST_SOURCES})
