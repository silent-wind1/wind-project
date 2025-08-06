<template>
  <div id="userManageView">
    <!-- 搜索表单 -->
    <a-form layout="inline" :model="searchParams" @finish="doSearch">
      <a-form-item label="账号">
        <a-input v-model:value="searchParams.userAccount" placeholder="输入账号" />
      </a-form-item>
      <a-form-item label="用户名">
        <a-input v-model:value="searchParams.userName" placeholder="输入用户名" />
      </a-form-item>
      <a-form-item>
        <a-button type="primary" html-type="submit">搜索</a-button>
      </a-form-item>
    </a-form>
    <a-divider />
    <!-- 表格 -->
    <a-table
      :columns="columns"
      :data-source="data"
      :pagination="pagination"
      @change="doTableChange"
    >
      <template #headerCell="{ column }">
        <template v-if="column.key === 'name'">
          <span>
            <smile-outlined />
            Name
          </span>
        </template>
      </template>
      <template #bodyCell="{ column, record }">
        <template v-if="column.dataIndex === 'userAvatar'">
          <a-image :src="record.userAvatar" :width="120" />
        </template>
        <template v-else-if="column.dataIndex === 'userRole'">
          <div v-if="record.userRole === 'admin'">
            <a-tag color="green">管理员</a-tag>
          </div>
          <div v-else>
            <a-tag color="blue">普通用户</a-tag>
          </div>
        </template>
        <template v-else-if="column.dataIndex === 'createTime'">
          {{ dayjs(record.createTime).format('YYYY-MM-DD HH:mm:ss') }}
        </template>
        <template v-else-if="column.key === 'operation'">
          <div class="editable-row-operations">
            <a @click="edit(record)">编辑</a>
          </div>
        </template>
        <template v-else-if="column.key === 'action'">
          <a-button danger @click="doDelete(record.id)">删除</a-button>
        </template>
      </template>
    </a-table>

    <!-- 编辑对话框 -->
    <a-modal
      v-model:open="editDialogVisible"
      title="编辑用户信息"
      ok-text="保存"
      cancel-text="取消"
      @ok="handleEditSubmit"
      @cancel="handleEditCancel"
    >
      <a-form :model="editFormState" layout="vertical">
        <a-form-item label="id">
          <a-input v-model:value="editFormState.id" disabled />
        </a-form-item>
        <a-form-item label="账号">
          <a-input v-model:value="editFormState.userAccount" />
        </a-form-item>
        <a-form-item label="头像">
            <a-upload
              v-model:value="editFormState.userAvatar"
              name="avatar"
              list-type="picture-card"
              class="avatar-uploader"
              :show-upload-list="false"
              :before-upload="beforeUpload"
              :customRequest="customUpload"
              @change="handleUploadChange"
            >
              <img
                v-if="editFormState.userAvatar"
                :src="editFormState.userAvatar"
                alt="avatar"
                class="avatar-preview"
              />
              <div v-else>
                <loading-outlined v-if="avatarLoading"></loading-outlined>
                <plus-outlined v-else></plus-outlined>
                <div class="ant-upload-text"></div>
              </div>
            </a-upload>
        </a-form-item>
        <a-form-item label="用户名" required>
          <a-input v-model:value="editFormState.userName" placeholder="请输入用户名" />
        </a-form-item>
        <a-form-item label="简介">
          <a-textarea v-model:value="editFormState.userProfile" placeholder="请输入简介" />
        </a-form-item>
        <a-form-item label="用户角色" required>
          <a-select v-model:value="editFormState.userRole">
            <a-select-option value="admin">管理员</a-select-option>
            <a-select-option value="user">普通用户</a-select-option>
          </a-select>
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>
<script lang="ts" setup>
import { SmileOutlined, PlusOutlined, LoadingOutlined } from '@ant-design/icons-vue'
import { computed, onMounted, reactive, ref } from 'vue'
import { deleteUser, listUserVoByPage, updateUser } from '@/api/userController.ts'
import type { UploadChangeParam } from 'ant-design-vue'
import { message } from 'ant-design-vue'
import dayjs from 'dayjs'
import { uploadFile } from '@/api/fileController.ts'

const columns = [
  {
    title: 'id',
    dataIndex: 'id',
  },
  {
    title: '账号',
    dataIndex: 'userAccount',
  },
  {
    title: '用户名',
    dataIndex: 'userName',
  },
  {
    title: '头像',
    dataIndex: 'userAvatar',
  },
  {
    title: '简介',
    dataIndex: 'userProfile',
  },
  {
    title: '用户角色',
    dataIndex: 'userRole',
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
  },
  {
    title: '编辑',
    key: 'operation',
  },
  {
    title: '操作',
    key: 'action',
  },
]

// 数据
const data = ref<API.UserVO[]>([])

const total = ref(0)

const searchParams = reactive<API.UserQueryRequest>({
  pageNum: 1,
  pageSize: 5,
})

const fetchData = async () => {
  const res = await listUserVoByPage({
    ...searchParams,
  })
  if (res.data.data) {
    data.value = res.data.data.records ?? []
    total.value = res.data.data.totalRow ?? 0
  } else {
    message.error('获取数据失败' + res.data.message)
  }
}

// 分页参数
const pagination = computed(() => {
  return {
    current: searchParams.pageNum ?? 1,
    pageSize: searchParams.pageSize ?? 10,
    total: total.value,
    showSizeChanger: true,
    showTotal: (total: number) => `共 ${total} 条`,
  }
})

// 表格变化处理
const doTableChange = (page: any) => {
  searchParams.pageNum = page.current
  searchParams.pageSize = page.pageSize
  fetchData()
}

const doSearch = () => {
  searchParams.pageNum = 1
  searchParams.pageSize = 5
  fetchData()
}

// 删除数据
const doDelete = async (id: string) => {
  if (!id) {
    return
  }
  const res = await deleteUser({ id })
  if (res.data.code === 0) {
    message.success(res.data.message)
    // 刷新数据
    fetchData()
  } else {
    message.error(res.data.message)
  }
}

// 编辑相关状态
const editDialogVisible = ref(false)
const editFormState = reactive<API.UserVO>({
  id: undefined,
  userAccount: '',
  userName: '',
  userAvatar: '',
  userProfile: '',
  userRole: undefined,
  createTime: '',
})

// 编辑操作
const edit = (record: API.UserVO) => {
  console.log(record)
  // 将当前行数据填充到表单
  Object.assign(editFormState, record)
  editDialogVisible.value = true
}

// 提交编辑
const handleEditSubmit = async () => {
  try {
    // 调用更新接口
    const res = await updateUser(editFormState)
    if (res.data.code === 0) {
      message.success('更新成功')
      // 刷新表格数据
      fetchData()
      // 关闭对话框
      editDialogVisible.value = false
    } else {
      message.error('更新失败: ' + res.data.message)
    }
  } catch (e) {
    message.error('更新请求失败')
    console.error(e)
  }
}

// 取消编辑
const handleEditCancel = () => {
  editDialogVisible.value = false
}

// 头像上传状态
const avatarLoading = ref(false)

// 头像上传前处理
const beforeUpload = (file: any) => {
  const isImage = file.type.startsWith('image/')
  if (!isImage) {
    message.error('只能上传图片文件！')
  }
  const isLt2M = file.size / 1024 / 1024 < 2
  if (!isLt2M) {
    message.error('图片大小不能超过 2MB!')
  }
  return isImage && isLt2M
}

// 添加自定义上传方法
const customUpload = async (options: any) => {
  const { file, onSuccess, onError } = options
  try {
    const res = await uploadFile(file, {})
    if (res.data.code === 0) {
      // 假设后端返回的URL字段是 data
      const url = res.data.data
      onSuccess(url) // 通知上传成功
      return url
    } else {
      onError(new Error(res.data.message))
    }
  } catch (error) {
    onError(error)
  }
}

// 处理头像上传变化
// 2. 修改handleUploadChange方法
const handleUploadChange = (info: UploadChangeParam) => {
  if (info.file.status === 'uploading') {
    avatarLoading.value = true
    return
  }

  if (info.file.status === 'done') {
    // 直接从响应中获取URL
    editFormState.userAvatar = info.file.response
    message.success('头像上传成功')
    avatarLoading.value = false
  } else if (info.file.status === 'error') {
    message.error('头像上传失败')
    avatarLoading.value = false
  }
}

onMounted(() => {
  fetchData()
})
</script>

<style>
#userManageView {
  //width: 1024px;
  text-align: center;
}

.avatar-preview {
  width: 100px;
  height: 100px;
  object-fit: cover;
  padding: 4px;
}
</style>
