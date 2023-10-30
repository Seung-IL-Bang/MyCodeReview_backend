#echo ">>> Start BeforeInstall"
#
#echo ">>> 기존 Revision 백업 생성"
#sudo cp -r /home/ubuntu/build/ /home/ubuntu/app/backup/
#
#echo ">>> 기존 Revision 원본 삭제"
#sudo rm -rf /home/ubuntu/build
#
#cd /home/ubuntu/app/backup
#
#DATE=$(date +'%Y-%m-%d_%H:%M:%S')
#
#sudo mv build backup-"$DATE"
#
#echo ">>> End BeforeInstall"