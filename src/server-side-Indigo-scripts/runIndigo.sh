INDIGO_HOME=/indigoapp/IndigoInstallation/
INDIGO_LIB=${INDIGO_HOME}/lib

JAVA_HOME=/exlibris/dps/d4_1/product/local/java
JAVA_CMD=${JAVA_HOME}/bin/amd64/java

echo Java is loaded from ${JAVA_CMD}
echo Java version is
${JAVA_CMD} -version

exec ${JAVA_CMD} -Xmx4096m -cp .:${INDIGO_HOME}:${INDIGO_LIB}/ManualDeposit-2.6-SNAPSHOT.jar:${INDIGO_LIB}/dependency/* nz.govt.natlib.ndha.manualdeposit.App $* | tee screen.log

