const express = require('express');
const path = require('path');

const app = express();
const port = process.env.PORT || 3000;

// 配置静态文件目录
app.use(express.static(path.join(__dirname, 'build')));

// 处理根路径请求
app.get('/', (req, res) => {
    res.sendFile(path.join(__dirname, 'build', 'index.html'));
});

// 启动服务器
app.listen(port, () => {
    console.log(`Server is running on port ${port}`);
});
