# MySQL数据库备份功能使用文档

## 📋 功能说明

本功能提供MySQL数据库的备份管理，支持：
- ✅ **手动备份**：通过API接口随时触发备份
- ✅ **自动备份**：基于Cron表达式定时自动备份
- ✅ **OSS上传**：自动上传备份文件到阿里云OSS
- ✅ **智能清理**：自动清理超过保留期的本地备份
- ✅ **备份列表**：查看所有本地备份文件
- ✅ **灵活配置**：支持全库备份或指定数据库备份

## 🚀 部署步骤

### 1. 添加配置

在Nacos配置中心或 `application.yml` 添加以下配置：

```yaml
# MySQL备份配置
mysql:
  backup:
    container-name: mysql           # MySQL容器名称
    username: root                  # MySQL用户名
    password: youyu1233            # MySQL密码
    local-backup-dir: /data/backups/mysql  # 本地备份目录
    oss-backup-path: /backup/mysql  # OSS备份路径
    retention-days: 7               # 本地保留天数
    enable-auto-backup: true        # 启用自动备份
    cron-expression: 0 0 20 ? * SUN # 每周日晚上8点
```

### 2. 创建备份目录

在服务器上创建备份目录：

```bash
mkdir -p /data/backups/mysql
chmod 755 /data/backups/mysql
```

### 3. 确保Docker可访问

确保应用服务器可以执行docker命令：

```bash
# 测试docker命令
docker ps | grep mysql

# 如果无权限，添加应用用户到docker组
sudo usermod -aG docker your-app-user
```

### 4. 重启服务

重启 `infra-service` 服务，查看日志确认定时任务启动：

```
MySQL自动备份定时任务已启动，cron表达式: 0 0 20 ? * SUN
```

## 📡 API接口文档

### 1. 手动备份数据库

**接口地址：** `POST /backup/mysql`

**请求体：**

```json
{
  "uploadToOss": true,           // 是否上传到OSS（可选，默认true）
  "databases": ["db1", "db2"],   // 要备份的数据库列表（可选，为空则备份所有）
  "remark": "重要功能上线前备份"    // 备份备注（可选）
}
```

**响应示例：**

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "fileName": "mysql_backup_20251215_200000.sql.gz",
    "localPath": "/data/backups/mysql/mysql_backup_20251215_200000.sql.gz",
    "ossPath": "/backup/mysql/mysql_backup_20251215_200000.sql.gz",
    "fileSize": 1048576,
    "fileSizeReadable": "1.00 MB",
    "backupTime": "2025-12-15T20:00:00",
    "uploadedToOss": true,
    "duration": 5234,
    "remark": "重要功能上线前备份"
  }
}
```

### 2. 获取备份列表

**接口地址：** `GET /backup/mysql/list`

**响应示例：**

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": [
    {
      "fileName": "mysql_backup_20251215_200000.sql.gz",
      "localPath": "/data/backups/mysql/mysql_backup_20251215_200000.sql.gz",
      "fileSize": 1048576,
      "fileSizeReadable": "1.00 MB",
      "backupTime": "2025-12-15T20:00:00"
    },
    {
      "fileName": "mysql_backup_20251208_200000.sql.gz",
      "localPath": "/data/backups/mysql/mysql_backup_20251208_200000.sql.gz",
      "fileSize": 987654,
      "fileSizeReadable": "964.51 KB",
      "backupTime": "2025-12-08T20:00:00"
    }
  ]
}
```

### 3. 清理过期备份

**接口地址：** `DELETE /backup/mysql/clean`

**响应示例：**

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "deletedCount": 3,
    "message": "已清理 3 个过期备份文件"
  }
}
```

## 🎨 前端调用示例

### Vue 3 + Axios 示例

```vue
<template>
  <div class="backup-manager">
    <h2>MySQL数据库备份管理</h2>

    <!-- 手动备份按钮 -->
    <div class="backup-actions">
      <el-button
        type="primary"
        :loading="backupLoading"
        @click="handleBackup">
        立即备份
      </el-button>

      <el-button
        type="warning"
        @click="handleCleanBackups">
        清理过期备份
      </el-button>

      <el-button
        @click="loadBackupList">
        刷新列表
      </el-button>
    </div>

    <!-- 备份列表 -->
    <el-table :data="backupList" style="margin-top: 20px;">
      <el-table-column prop="fileName" label="文件名" width="300" />
      <el-table-column prop="fileSizeReadable" label="文件大小" width="120" />
      <el-table-column prop="backupTime" label="备份时间" width="200" />
      <el-table-column label="操作" width="150">
        <template #default="{ row }">
          <el-button
            size="small"
            @click="downloadBackup(row)">
            下载
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from 'axios'

const backupLoading = ref(false)
const backupList = ref([])

// 执行备份
const handleBackup = async () => {
  try {
    await ElMessageBox.confirm('确认要备份数据库吗？', '提示', {
      type: 'warning'
    })

    backupLoading.value = true
    const { data } = await axios.post('/backup/mysql', {
      uploadToOss: true,
      remark: '用户手动备份'
    })

    if (data.code === 200) {
      ElMessage.success({
        message: `备份成功！文件: ${data.data.fileName}，大小: ${data.data.fileSizeReadable}`,
        duration: 5000
      })
      loadBackupList()
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('备份失败：' + (error.response?.data?.msg || error.message))
    }
  } finally {
    backupLoading.value = false
  }
}

// 加载备份列表
const loadBackupList = async () => {
  try {
    const { data } = await axios.get('/backup/mysql/list')
    if (data.code === 200) {
      backupList.value = data.data
    }
  } catch (error) {
    ElMessage.error('加载备份列表失败')
  }
}

// 清理过期备份
const handleCleanBackups = async () => {
  try {
    await ElMessageBox.confirm('确认要清理过期备份吗？', '提示', {
      type: 'warning'
    })

    const { data } = await axios.delete('/backup/mysql/clean')
    if (data.code === 200) {
      ElMessage.success(data.data.message)
      loadBackupList()
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('清理失败')
    }
  }
}

// 下载备份（需要后端提供下载接口或通过其他方式）
const downloadBackup = (row) => {
  ElMessage.info('下载功能待实现')
}

onMounted(() => {
  loadBackupList()
})
</script>
```

### React + Fetch 示例

```jsx
import React, { useState, useEffect } from 'react';
import { Button, Table, message } from 'antd';

export default function BackupManager() {
  const [backupList, setBackupList] = useState([]);
  const [loading, setLoading] = useState(false);

  // 加载备份列表
  const loadBackupList = async () => {
    try {
      const response = await fetch('/backup/mysql/list');
      const data = await response.json();
      if (data.code === 200) {
        setBackupList(data.data);
      }
    } catch (error) {
      message.error('加载备份列表失败');
    }
  };

  // 执行备份
  const handleBackup = async () => {
    setLoading(true);
    try {
      const response = await fetch('/backup/mysql', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          uploadToOss: true,
          remark: '用户手动备份'
        })
      });
      const data = await response.json();

      if (data.code === 200) {
        message.success(`备份成功！文件: ${data.data.fileName}`);
        loadBackupList();
      }
    } catch (error) {
      message.error('备份失败');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadBackupList();
  }, []);

  const columns = [
    { title: '文件名', dataIndex: 'fileName', key: 'fileName' },
    { title: '文件大小', dataIndex: 'fileSizeReadable', key: 'fileSizeReadable' },
    { title: '备份时间', dataIndex: 'backupTime', key: 'backupTime' },
  ];

  return (
    <div>
      <Button type="primary" loading={loading} onClick={handleBackup}>
        立即备份
      </Button>
      <Button onClick={loadBackupList} style={{ marginLeft: 8 }}>
        刷新列表
      </Button>
      <Table
        columns={columns}
        dataSource={backupList}
        rowKey="fileName"
        style={{ marginTop: 20 }}
      />
    </div>
  );
}
```

## 🔄 恢复数据

### 从本地备份恢复

```bash
# 1. 找到备份文件
ls /data/backups/mysql/

# 2. 解压并恢复
gunzip < /data/backups/mysql/mysql_backup_20251215_200000.sql.gz | \
  docker exec -i mysql mysql -uroot -pyouyu1233
```

### 从OSS恢复

```bash
# 1. 先从OSS下载（需要安装ossutil）
ossutil cp oss://your-bucket/backup/mysql/mysql_backup_20251215_200000.sql.gz /tmp/

# 2. 解压并恢复
gunzip < /tmp/mysql_backup_20251215_200000.sql.gz | \
  docker exec -i mysql mysql -uroot -pyouyu1233
```

## ⚙️ 配置说明

### Cron表达式示例

| 表达式 | 说明 |
|--------|------|
| `0 0 2 * * ?` | 每天凌晨2点 |
| `0 0 20 ? * SUN` | 每周日晚上8点 |
| `0 0 20 ? * WED,SUN` | 每周三、周日晚上8点 |
| `0 0 3 1 * ?` | 每月1号凌晨3点 |
| `0 0 0/4 * * ?` | 每4小时执行一次 |

### 保留天数建议

- **开发环境**：3-7天
- **测试环境**：7-14天
- **生产环境**：7-30天（本地） + 长期（OSS）

## 🛡️ 安全建议

1. **密码安全**
   - 不要在配置文件中明文存储MySQL密码
   - 使用环境变量：`password: ${MYSQL_PASSWORD}`
   - 使用配置中心加密功能

2. **访问控制**
   - 备份接口建议添加权限验证
   - 限制只有管理员可以执行备份操作

3. **OSS权限**
   - 使用最小权限原则的AccessKey
   - 定期轮换AccessKey
   - 建议使用RAM角色（服务器在阿里云）

4. **备份验证**
   - 定期测试备份恢复流程
   - 监控备份任务执行状态

## ❓ 常见问题

**Q: 备份失败，提示"MySQL容器未运行"**
A: 检查配置中的`container-name`是否正确，使用`docker ps`查看实际容器名称

**Q: 上传OSS失败**
A: 检查OSS配置是否正确，网络是否可达OSS节点

**Q: 如何备份单个数据库？**
A: 在请求体中指定：`{"databases": ["your_db_name"]}`

**Q: 定时任务没有执行**
A: 检查配置`enable-auto-backup: true`，查看日志确认定时任务是否启动

**Q: 如何在Windows服务器上使用？**
A: 需要修改`MysqlBackupServiceImpl`中的命令执行方式，使用PowerShell或cmd替代sh

## 📝 更新日志

- **v1.0.0** (2025-12-15)
  - ✅ 支持手动和自动备份
  - ✅ 支持OSS上传
  - ✅ 支持自动清理过期备份
  - ✅ 提供REST API接口
  - ✅ 支持全库或指定数据库备份