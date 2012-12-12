PLAY_HOME=/home/tcommerce/play-1.2.5
PLAY=$PLAY_HOME/play
TCOMMERCE_SRC=/home/tcommerce/tcommerce
TCOMMERCE_PROD=/home/tcommerce/tcommerce_prod

git pull

cd $TCOMMERCE_PROD
$PLAY stop $TCOMMERCE_PROD

rm -Rf $TCOMMERCE_PROD
mkdir $TCOMMERCE_PROD
cp -R $TCOMMERCE_SRC/* $TCOMMERCE_PROD/
rm $TCOMMERCE_PROD/README.md
rm $TCOMMERCE_PROD/build.sh
sed 's/application.mode=dev/application.mode=prod/' -i $TCOMMERCE_PROD/conf/application.conf
cd $TCOMMERCE_PROD

$PLAY start -Dhttp.port=9010
