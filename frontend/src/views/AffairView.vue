<template>
  <div class="affair-container">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <span>创建事务消息</span>
      </div>
      <el-form :model="form" label-width="120px">
        <el-form-item label="方法 (Method)">
          <el-input v-model="form.method" placeholder="例如：create"></el-input>
        </el-form-item>
        
        <el-divider content-position="left">参数 (Params)</el-divider>
        <el-form-item label="能力 (Ability)">
          <el-input v-model="form.params.ability" placeholder="Ability"></el-input>
        </el-form-item>
        <el-form-item label="发送时间">
          <el-date-picker
            v-model="form.params.sendTime"
            type="datetime"
            value-format="yyyy-MM-dd HH:mm:ss"
            placeholder="选择日期时间">
          </el-date-picker>
        </el-form-item>

        <el-divider content-position="left">事件列表 (Events)</el-divider>
        <div v-for="(event, index) in form.params.events" :key="index" class="event-block">
          <el-card shadow="never" class="event-card">
            <div slot="header" class="clearfix">
              <span>事件 #{{ index + 1 }}</span>
              <el-button style="float: right; padding: 3px 0" type="text" @click="removeEvent(index)">移除</el-button>
            </div>
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="事件ID" label-width="100px">
                  <el-input v-model="event.eventId"></el-input>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="事件类型" label-width="100px">
                  <el-input v-model="event.eventType"></el-input>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="发生时间" label-width="100px">
                   <el-date-picker
                    v-model="event.happenTime"
                    type="datetime"
                    value-format="yyyy-MM-dd HH:mm:ss"
                    placeholder="选择日期时间">
                  </el-date-picker>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="源名称" label-width="100px">
                  <el-input v-model="event.srcName"></el-input>
                </el-form-item>
              </el-col>
            </el-row>
             <el-row :gutter="20">
              <el-col :span="8">
                <el-form-item label="源索引" label-width="100px">
                  <el-input v-model="event.srcIndex"></el-input>
                </el-form-item>
              </el-col>
               <el-col :span="8">
                <el-form-item label="父索引" label-width="100px">
                  <el-input v-model="event.srcParentIndex"></el-input>
                </el-form-item>
              </el-col>
               <el-col :span="8">
                <el-form-item label="源类型" label-width="100px">
                  <el-input v-model="event.srcType"></el-input>
                </el-form-item>
              </el-col>
            </el-row>
             <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="状态" label-width="100px">
                  <el-input-number v-model="event.status"></el-input-number>
                </el-form-item>
              </el-col>
               <el-col :span="12">
                <el-form-item label="超时时间" label-width="100px">
                  <el-input-number v-model="event.timeout"></el-input-number>
                </el-form-item>
              </el-col>
            </el-row>
            <el-form-item label="数据 (JSON)" label-width="100px">
              <el-input type="textarea" v-model="event.dataStr" rows="3" placeholder='{"key": "value"}'></el-input>
            </el-form-item>
          </el-card>
        </div>
        <div style="margin-top: 10px; margin-bottom: 20px;">
          <el-button type="primary" plain icon="el-icon-plus" @click="addEvent">添加事件</el-button>
        </div>

        <el-form-item>
          <el-button type="success" @click="onSubmitDefault">添加默认事务</el-button>
          <el-button type="warning" @click="onSubmitTransaction">添加事务性消息</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import { addAffairDefault, addAffairTransaction } from '@/api/affair'

export default {
  name: 'AffairView',
  data() {
    return {
      form: {
        method: '',
        params: {
          ability: '',
          sendTime: '',
          events: []
        }
      }
    }
  },
  methods: {
    addEvent() {
      this.form.params.events.push({
        eventId: '',
        eventType: '',
        happenTime: '',
        srcIndex: '',
        srcName: '',
        srcParentIndex: '',
        srcType: '',
        status: 0,
        timeout: 0,
        dataStr: '{}', // Temporary storage for JSON string
        data: {}
      })
    },
    removeEvent(index) {
      this.form.params.events.splice(index, 1)
    },
    prepareData() {
      // Deep copy to avoid modifying the form directly during parsing
      const dataToSend = JSON.parse(JSON.stringify(this.form))
      
      // Parse dataStr to JSON object for each event
      try {
        dataToSend.params.events.forEach(event => {
          if (event.dataStr) {
            event.data = JSON.parse(event.dataStr)
            delete event.dataStr
          }
        })
      } catch (e) {
        this.$message.error('事件数据 JSON 格式错误')
        return null
      }
      return dataToSend
    },
    onSubmitDefault() {
      const data = this.prepareData()
      if (!data) return
      
      addAffairDefault(data).then(() => {
        this.$message.success('默认事务添加成功')
      })
    },
    onSubmitTransaction() {
      const data = this.prepareData()
      if (!data) return

      addAffairTransaction(data).then(() => {
         this.$message.success('事务性消息添加成功')
      })
    }
  }
}
</script>

<style scoped>
.event-block {
  margin-bottom: 10px;
}
.event-card {
  background-color: #fafafa;
}
</style>
