PLAY_HOME=/home/tcommerce/play-1.2.5
PLAY=$PLAY_HOME/play
TCOMMERCE_SRC=/home/tcommerce/tcommerce
TCOMMERCE_PROD=/home/tcommerce/tcommerce_prod

$PLAY stop $TCOMMERCE_HOME
git pull
rm -Rf $TCOMMERCE_PROD
mkdir $TCOMMERCE_PROD
cp -R $TCOMMERCE_SRC/* $TCOMMERCE_PROD/
