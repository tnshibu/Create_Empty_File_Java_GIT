CALL SET_JAVA_HOME.bat
echo on
%JAVA_HOME%\bin\javac -cp . CreateEmptyFile.java
%JAVA_HOME%\bin\java  -cp . CreateEmptyFile %1
