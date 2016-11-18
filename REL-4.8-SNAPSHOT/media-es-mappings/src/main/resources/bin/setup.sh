#!/usr/bin/env bash

if [ "$DEBUG" = "true" ] ; then
    set -x
fi

if [ $# -lt 1 ];
then
    echo "Usage $0 npo_dev|npo_test|npo_prod|localhost|<es-url> [<index number>|<alias>]"
    echo "index number:  Number of the new index to create (e.g. 2 in apimedia-2). If ommited the mappings are put over the old ones (only possible if they are compatible)"
    exit
fi


case "$1" in

    npo_dev)
        desthost=http://es-dev.poms.omroep.nl
        ;;
    npo_test)
        desthost=http://es-test.poms.omroep.nl
        ;;
    npo_prod)
        desthost=http://poms10aas:9200
        ;;
    localhost)
        desthost="http://localhost:9200"
        ;;
    *)
        echo "Unknown destination. Supposing that $1 is the URL."
        desthost=$1
        ;;
esac

basedir="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && cd .. && pwd )"

if [ "$2" == "" ] ; then
    echo "No index number found, trying to put mappings over existing ones (supposing they are compatible)"
    destindex=apimedia
else
    if [[ $2 =~ ^[0-9]+$ ]] ; then
        previndex=apimedia-$(($2-1))
        destindex=apimedia-$2
    else
        echo "No index number found, trying to put mappings over existing ones (supposing they are compatible)"
        destindex=$2
    fi

fi

echo "Echo putting $basedir to $desthost/$destindex"
if [ "$previndex" != "" ]; then
    echo "putting settings"
    curl -XPUT $desthost/$destindex -d @$basedir/setting/apimedia.json
fi

echo

declare -a arr=( "group" "program" "segment" "deletedprogram" "deletedgroup" "deletedsegment" "cue" "programMemberRef" "groupMemberRef" "segmentMemberRef" "episodeRef" )

for mapping in "${arr[@]}"
do
    echo $mapping
    curl -XPUT $desthost/$destindex/$mapping/_mapping -d @$basedir/mapping/$mapping.json
    echo
done

echo

if [ "$previndex" != "" ] ; then

   echo "moving alias $previndex $destindex"

   publishalias="{
    \"actions\": [
        { \"remove\": {
            \"alias\": \"apimedia-publish\",
            \"index\": \"$previndex\"
        }},
        { \"add\": {
            \"alias\": \"apimedia-publish\",
            \"index\": \"$destindex\"
        }}
    ]
}
"
   echo $publishalias

   curl -XPOST $desthost/_aliases -d "$publishalias"


   echo -e "\nConsider:\n stream2es es  --source $desthost/$previndex --target $desthost/$destindex"
   echo "(streams2es can be found at https://github.com/elasticsearch/stream2es)"

   alias="{
    \"actions\": [
        { \"remove\": {
            \"alias\": \"apimedia\",
            \"index\": \"$previndex\"
        }},
        { \"add\": {
            \"alias\": \"apimedia\",
            \"index\": \"$destindex\"
        }}
    ]
}
"

   echo "Followed by"
   echo curl -XPOST $desthost/_aliases -d "'$alias'"
fi
