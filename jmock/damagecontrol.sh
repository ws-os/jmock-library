#!/bin/sh
# Damage control build script for jMock.

#export RELEASE_ID=1.0
#export PRERELEASE_ID=1.0-RC1
export SNAPSHOT_ID=$(date --utc +%Y%m%d-%H%M%S)

export PACKAGEDIR=packages
export WEBDIR=website/output
export JAVADOCDIR=$WEBDIR/docs/javadoc

export WEBSITE=dcontrol@www.codehaus.org:/www/jmock.codehaus.org/
export DISTSITE=dcontrol@dist.codehaus.org:/www/dist.codehaus.org/jmock

function run_task {
	local task=$1
	
	if sh continuous-integration/$task.sh > /dev/null; then
		echo $task done;
	else
		echo $task failed;
		exit 1
	fi
}

function tasks {
	for task in $*; do
		run_task $task;
	done
}

tasks build-website build-javadocs build-source-snapshots

# deploy by default
if let ${DEPLOY:-1}; then
	tasks deploy-website deploy-snapshots;
fi

echo all done.