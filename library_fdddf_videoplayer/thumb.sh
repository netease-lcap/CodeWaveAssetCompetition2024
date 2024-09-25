#!/bin/bash

# 检查是否提供了视频文件
if [ -z "$1" ]; then
  echo "Usage: $0 <video_file>"
  exit 1
fi

# 获取视频文件名
video_file="$1"

# 获取视频时长（秒）
duration=$(ffprobe -v error -show_entries format=duration -of default=noprint_wrappers=1:nokey=1 "$video_file")

# 设置 fps，每秒生成一张缩略图
fps=0.5

# 计算总的缩略图数量
thumbnail_count=$(echo "$duration * $fps" | bc)

# 设置每行的列数
columns=10

# 计算需要的行数
rows=$(echo "($thumbnail_count + $columns - 1) / $columns" | bc)

# 生成缩略图图集
ffmpeg -y -i "$video_file" -vf "fps=$fps,scale=160:90,tile=${columns}x${rows}" thumbnails.jpg

echo "NUM: $thumbnail_count"
echo "COL: $columns"
