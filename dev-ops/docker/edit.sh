# 查看 docker.service
cat /usr/lib/systemd/system/docker.service
# 或者查看这个路径下的 docker.service
cat /lib/systemd/system/docker.service

# 修改 docker.service
vim /usr/lib/systemd/system/docker.service
# 或者修改这个路径下的 docker.service，我的腾讯云服务器修改了这个路径下的文件
vim /lib/systemd/system/docker.service

# 修改内容
```shell
ExecStart=/usr/bin/dockerd -H fd:// --containerd=/run/containerd/containerd.sock 修改为 ExecStart=/usr/bin/dockerd -H tcp://0.0.0.0:2375 -H fd:// --containerd=/run/containerd/containerd.sock
```
# 或者修改内容，我的腾讯云服务器修改了这些内容
```shell
ExecStart=/usr/bin/dockerd-current \ 修改为 ExecStart=/usr/bin/dockerd-current -H tcp://0.0.0.0:2375 -H unix://var/run/docker.sock \
```

# 重启服务
systemctl daemon-reload && systemctl restart docker

# 查看监听端口和进程
ps -ef | grep docker

ss -ntl | grep 2375

```shell
[root@chatgpt-guide ~]# ps -ef | grep docker
root     13091     1  0 01:33 ?        00:00:00 /usr/bin/dockerd -H tcp://0.0.0.0:2375 -H fd:// --containerd=/run/containerd/containerd.sock
root     13218 13091  0 01:33 ?        00:00:00 /usr/bin/docker-proxy -proto tcp -host-ip 0.0.0.0 -host-port 9000 -container-ip 172.17.0.2 -container-port 9000
root     13222 13091  0 01:33 ?        00:00:00 /usr/bin/docker-proxy -proto tcp -host-ip :: -host-port 9000 -container-ip 172.17.0.2 -container-port 9000
root     13234 13091  0 01:33 ?        00:00:00 /usr/bin/docker-proxy -proto tcp -host-ip 0.0.0.0 -host-port 443 -container-ip 172.17.0.3 -container-port 443
root     13238 13091  0 01:33 ?        00:00:00 /usr/bin/docker-proxy -proto tcp -host-ip :: -host-port 443 -container-ip 172.17.0.3 -container-port 443
root     13276 13091  0 01:33 ?        00:00:00 /usr/bin/docker-proxy -proto tcp -host-ip 0.0.0.0 -host-port 80 -container-ip 172.17.0.3 -container-port 80
root     13285 13091  0 01:33 ?        00:00:00 /usr/bin/docker-proxy -proto tcp -host-ip :: -host-port 80 -container-ip 172.17.0.3 -container-port 80
root     13416 12656  0 01:34 pts/1    00:00:00 grep --color=auto docker
[root@chatgpt-guide ~]# ss -ntl | grep 2375
LISTEN     0      128       [::]:2375                  [::]:*
[root@chatgpt-guide ~]#
```
