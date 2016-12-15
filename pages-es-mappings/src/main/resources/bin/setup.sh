#!/usr/bin/env bash
#set -x

case "$1" in

    vpro_test)
        desthost=http://vs-elsearch-01.vpro.nl:9200
        ;;
    npo_dev)
        desthost=http://es-dev.pages.omroep.nl
        ;;
    npo_test)
        desthost=http://es-test.pages.omroep.nl
        ;;
    npo_prod)
        #desthost=http://es.pages.omroep.nl
        desthost=http://localhost:9208
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

if [ $# -lt 1 ];
then
    echo "Usage $0 vpro_test|npo_dev|npo_test|npo_prod|localhost|<es-url> [<index number>]"
    echo "index name:  Number of the new index to create (e.g. 2 in apipages-2)"
    exit
fi


if [ "$2" == "" ] ; then
    echo "No index number found, trying to put mappings over existing ones (supposing they are compatible)"
    destindex=apipages-publish
else
    previndex=apipages-$(($2-1))
    destindex=apipages-$2
fi




echo "Echo putting $basedir to $desthost/$destindex"
if [ "$2" != "" ]; then
    curl -XPUT $desthost/$destindex -d @$basedir/setting/apipages.json
    echo " "
fi

curl -XPUT $desthost/$destindex/page/_mapping -d @$basedir/mapping/page.json

destalias="apipages-publish"

if  [ "$3" != "" ] ; then
    destalias=$3
fi

if [ "$2" != "" ] ; then

    echo " "

    echo "moving alias"

    publishalias="
{
    \"actions\": [
        { \"remove\": {
            \"alias\": \"$destalias\",
            \"index\": \"$previndex\"
        }},
        { \"add\": {
            \"alias\": \"$destalias\",
            \"index\": \"$destindex\"
        }}
    ]
}
"
    echo $publishalias


    curl -XPOST $desthost/_aliases -d "$publishalias"

    echo -e "\nConsider:\n stream2es es  --source $desthost/$previndex --target $desthost/$destindex"
    echo "streams2es can be found at https://github.com/elasticsearch/stream2es"


   alias="{
    \"actions\": [
        { \"remove\": {
            \"alias\": \"apipages\",
            \"index\": \"$previndex\"
        }},
        { \"add\": {
            \"alias\": \"apipages\",
            \"index\": \"$destindex\"
        }}
    ]
}
"

   echo "Followed by"
   echo curl -XPOST $desthost/_aliases -d "'$alias'"

fi
