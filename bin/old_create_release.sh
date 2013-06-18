#!/bin/sh

_rename_files ()
{
    extension=$1
    for file in `ls $ARTIFACTS/*${extension}`; do
        newfile=${file%%${extension}}
        newfile=$newfile-$VERSION"$extension"
        mv $file $newfile
    done
}

# check the arguments
VERSION=$1
if [ -z $VERSION ]; then
    echo "Version must be set."
    exit 1
fi

# be sure the checkout is up to date
svn up
if [ $? -gt 0 ]; then
    echo "SVN updating failed, aborting tagging process."
    exit 1
fi

TRUNK="trunk/"
TAG="tags/jpkg-$VERSION"

# verify the tag is new
if [ -d $TAG ]; then
    echo "Tag $TAG already exists, aborting."
    exit 1
fi

# create the tag
svn cp $TRUNK $TAG 
if [ $? -gt 0 ]; then
    echo "Creating the tag failed, aborting tagging process."
    exit 1
fi

# perform the build. unit tests need to be run from the base
pushd .
cd $TAG
ant distclean dist

# validate the build worked
if [ $? -gt 0 ]; then
    echo "Build failed, aborting tagging process."
    exit 1
fi

# back down to the root
popd

# add the javadocs to the tag since we need these to be served on google code
cp -r $TAG/dist/javadoc $TAG/javadoc
svn add $TAG/javadoc

# fix up the svn mimetypes for the html [needed by google code]
find $TAG/javadoc -name \*.html -exec svn propset svn:mime-type text/html {} \;

# create a versioned artifact directory and copy the build results there
ARTIFACTS="artifacts-$VERSION"
mkdir $ARTIFACTS
cp $TAG/dist/*.zip $ARTIFACTS

# rename the artifacts to include the version number
_rename_files ".zip"

svn commit -m "Created $TAG from trunk for release $VERSION." $TAG
