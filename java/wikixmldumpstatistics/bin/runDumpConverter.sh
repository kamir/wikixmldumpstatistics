#!/bin/sh


#
# startet den XML-Wiki-Dump-Converter 
#
# Stand: 16.7.2012
#
buildClassPath() {
        jar_dir=$1
        if [ $# -ne 1 ]; then
                echo "Jar directory must be specified."
                exit 1
        fi
        class_path=
        c=1
        for i in `ls $jar_dir/*.jar`
        do
                if [ "$c" -eq "1" ]; then
                        class_path=${i}
                        c=2
                else
                        class_path=${class_path}:${i}
                fi
        done
        echo $class_path
        #return $class_path
}

CP1=`buildClassPath ./../dist`
CP2=`buildClassPath ./../dist/lib`

echo $CP1
echo $CP2

#java -Xmx2560m -cp $CP1:$CP2 xmldumptools.Dump2HTMLConverter $1 $2 $3 $4 $5 $6 $7 $8 $9 
java -Xmx2560m -cp $CP1:$CP2 xmldumptools.DumpWordStatisticsCreator $1 $2 $3 $4 $5 $6 $7 $8 $9 
 
