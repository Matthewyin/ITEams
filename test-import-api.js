/**
 * IT资产管理系统 - Excel导入API测试脚本
 * 
 * 用于测试Excel导入接口的响应结构
 */

// 模拟表单数据上传
function testImportAPI() {
  const form = new FormData();
  
  // 创建一个简单的Excel文件（仅为测试）
  // 实际使用时，应该选择一个有效的Excel文件
  const fileInput = document.getElementById('fileInput');
  const file = fileInput.files[0];
  
  if (!file) {
    console.error('请先选择一个Excel文件');
    return;
  }
  
  form.append('file', file);
  
  // 发送请求到导入API
  fetch('/api/import/excel', {
    method: 'POST',
    body: form
  })
  .then(response => response.json())
  .then(data => {
    console.log('API响应:', data);
    
    // 检查响应结构
    if (data.success) {
      console.log('上传成功，任务ID:', data.data);
      
      // 使用任务ID查询进度
      if (data.data) {
        pollImportProgress(data.data);
      } else {
        console.error('响应中没有taskId');
      }
    } else {
      console.error('上传失败:', data.message);
    }
  })
  .catch(error => {
    console.error('请求错误:', error);
  });
}

// 轮询导入进度
function pollImportProgress(taskId) {
  console.log(`开始轮询任务进度, 任务ID: ${taskId}`);
  
  const progressInterval = setInterval(() => {
    fetch(`/api/import/progress/${taskId}`)
      .then(response => response.json())
      .then(data => {
        console.log('进度数据:', data);
        
        if (data.success) {
          const progress = data.data;
          console.log(`处理进度: ${Math.round(progress.progress * 100)}%, 状态: ${progress.state}`);
          
          // 如果任务完成或失败，停止轮询
          if (progress.state === 'COMPLETED' || progress.state === 'FAILED') {
            console.log('任务已完成, 停止轮询');
            clearInterval(progressInterval);
            
            // 如果有批次ID，获取导入结果
            if (progress.batchId) {
              getImportResult(progress.batchId);
            }
          }
        } else {
          console.error('获取进度失败:', data.message);
          clearInterval(progressInterval);
        }
      })
      .catch(error => {
        console.error('进度请求错误:', error);
        clearInterval(progressInterval);
      });
  }, 2000); // 每2秒轮询一次
}

// 获取导入结果
function getImportResult(batchId) {
  console.log(`获取导入结果, 批次ID: ${batchId}`);
  
  fetch(`/api/import/result/${batchId}`)
    .then(response => response.json())
    .then(data => {
      console.log('导入结果:', data);
      
      if (data.success) {
        const result = data.data;
        console.log(`总资产数: ${result.totalAssets}, 成功: ${result.successCount}, 失败: ${result.failedCount}`);
      } else {
        console.error('获取结果失败:', data.message);
      }
    })
    .catch(error => {
      console.error('结果请求错误:', error);
    });
}

// 添加HTML元素用于测试
document.addEventListener('DOMContentLoaded', () => {
  const testDiv = document.createElement('div');
  testDiv.innerHTML = `
    <h2>Excel导入API测试</h2>
    <input type="file" id="fileInput" accept=".xlsx,.xls" />
    <button onclick="testImportAPI()">测试导入</button>
    <div id="results"></div>
  `;
  document.body.appendChild(testDiv);
}); 