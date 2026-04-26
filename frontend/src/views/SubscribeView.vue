<template>
  <div class="subscribe-container">
    <el-tabs v-model="activeTab">
      <!-- Subscription Management Tab -->
      <el-tab-pane label="订阅管理" name="management">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <el-button type="primary" icon="el-icon-plus" @click="showAddDialog = true">添加订阅</el-button>
            <el-button type="info" icon="el-icon-refresh" @click="fetchSubscriptions" style="float: right;">刷新</el-button>
          </div>
          
          <el-table :data="subscriptions" style="width: 100%" border v-loading="loading">
            <el-table-column prop="id" label="ID" width="80"></el-table-column>
            <el-table-column prop="eventTypes" label="事件类型 (Event Types)"></el-table-column>
            <el-table-column prop="eventDest" label="事件目标 (Event Dest)"></el-table-column>
            <el-table-column prop="subType" label="订阅类型 (Sub Type)" width="150"></el-table-column>
            <el-table-column prop="eventLvl" label="事件等级 (Event Lvl)" width="150"></el-table-column>
            <el-table-column label="操作" width="120">
              <template slot-scope="scope">
                <el-button type="danger" size="mini" @click="handleDelete(scope.row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>

        <!-- Add Dialog -->
        <el-dialog title="添加订阅" :visible.sync="showAddDialog">
          <el-form :model="addForm" label-width="120px">
             <el-form-item label="事件类型">
              <el-input v-model="addForm.eventTypesStr" placeholder="逗号分隔，例如：type1,type2"></el-input>
            </el-form-item>
            <el-form-item label="事件目标">
              <el-input v-model="addForm.eventDest"></el-input>
            </el-form-item>
            <el-form-item label="订阅类型">
              <el-input-number v-model="addForm.subType"></el-input-number>
            </el-form-item>
            <el-form-item label="事件等级">
               <el-input v-model="addForm.eventLvlStr" placeholder="逗号分隔的整数，例如：1,2"></el-input>
            </el-form-item>
          </el-form>
          <div slot="footer" class="dialog-footer">
            <el-button @click="showAddDialog = false">取消</el-button>
            <el-button type="primary" @click="confirmAdd">确认</el-button>
          </div>
        </el-dialog>
      </el-tab-pane>

      <!-- Message Query Tab -->
      <el-tab-pane label="消息查询" name="query">
         <el-card class="box-card">
           <div slot="header">
             <span>获取消息</span>
           </div>
           <el-form :model="msgForm" inline>
             <el-form-item label="主题 (Topic)">
                <el-select
                 v-model="msgForm.topic"
                 filterable
                 allow-create
                 default-first-option
                 placeholder="选择或输入主题">
                 <el-option
                   v-for="item in uniqueTopics"
                   :key="item"
                   :label="item"
                   :value="item">
                 </el-option>
               </el-select>
             </el-form-item>
             <el-form-item label="URL">
                <el-select
                 v-model="msgForm.url"
                 filterable
                 allow-create
                 default-first-option
                 placeholder="选择或输入URL">
                 <el-option
                   v-for="item in uniqueUrls"
                   :key="item"
                   :label="item"
                   :value="item">
                 </el-option>
               </el-select>
             </el-form-item>
             <el-form-item label="数量 (N)">
               <el-input-number v-model="msgForm.N" :min="1" :max="100"></el-input-number>
             </el-form-item>
             <el-form-item>
               <el-button type="primary" @click="fetchMessages">查询</el-button>
             </el-form-item>
           </el-form>

           <div class="msg-list" v-if="queryEvents.length">
             <el-table 
                :data="pagedQueryEvents" 
                border 
                style="width: 100%" 
                @row-click="showDetail"
                highlight-current-row>
                <el-table-column type="index" label="序号" width="60"></el-table-column>
                <el-table-column prop="method" label="方法 (Method)" width="150"></el-table-column>
                <el-table-column prop="status" label="状态 (Status)" width="100"></el-table-column>
                <el-table-column prop="eventType" label="主题 (EventType)"></el-table-column>
                <el-table-column prop="eventId" label="事件ID (EventID)"></el-table-column>
                <el-table-column prop="happenTime" label="事件时间 (HappenTime)" width="180"></el-table-column>
             </el-table>
             <div style="margin-top: 15px; text-align: right;">
                <el-pagination
                  @size-change="handleQuerySizeChange"
                  @current-change="handleQueryCurrentChange"
                  :current-page="queryPage.currentPage"
                  :page-sizes="[5, 10, 20, 50]"
                  :page-size="queryPage.pageSize"
                  layout="total, sizes, prev, pager, next, jumper"
                  :total="queryEvents.length">
                </el-pagination>
             </div>
           </div>
           <div v-else class="empty-text">暂无消息</div>
         </el-card>
      </el-tab-pane>

      <!-- Latest Message Tab -->
      <el-tab-pane label="最新消息" name="latest">
         <el-card class="box-card">
           <div slot="header">
             <span>获取最新消息</span>
             <el-button style="float: right; padding: 3px 0" type="text" @click="fetchLatest">刷新</el-button>
           </div>
           <div class="msg-list" v-if="latestEvents.length">
              <el-table 
                :data="pagedLatestEvents" 
                border 
                style="width: 100%" 
                @row-click="showDetail"
                highlight-current-row>
                <el-table-column type="index" label="序号" width="60"></el-table-column>
                <el-table-column prop="method" label="方法 (Method)" width="150"></el-table-column>
                <el-table-column prop="status" label="状态 (Status)" width="100"></el-table-column>
                <el-table-column prop="eventType" label="主题 (EventType)"></el-table-column>
                <el-table-column prop="eventId" label="事件ID (EventID)"></el-table-column>
                <el-table-column prop="happenTime" label="事件时间 (HappenTime)" width="180"></el-table-column>
             </el-table>
             <div style="margin-top: 15px; text-align: right;">
                <el-pagination
                  @size-change="handleLatestSizeChange"
                  @current-change="handleLatestCurrentChange"
                  :current-page="latestPage.currentPage"
                  :page-sizes="[5, 10, 20, 50]"
                  :page-size="latestPage.pageSize"
                  layout="total, sizes, prev, pager, next, jumper"
                  :total="latestEvents.length">
                </el-pagination>
             </div>
           </div>
           <div v-else class="empty-text">暂无最新消息</div>
         </el-card>
      </el-tab-pane>
    </el-tabs>

    <!-- Detail Dialog -->
    <el-dialog title="详细事件信息" :visible.sync="detailDialogVisible" width="60%">
        <pre v-if="currentDetail">{{ currentDetail }}</pre>
        <span slot="footer" class="dialog-footer">
            <el-button type="primary" @click="detailDialogVisible = false">关闭</el-button>
        </span>
    </el-dialog>
  </div>
</template>

<script>
import { subscribeAdd, subscribeDelete, subscribeDetect, getMsg, getLatestMsg } from '@/api/subscribe'

export default {
  name: 'SubscribeView',
  data() {
    return {
      activeTab: 'management',
      loading: false,
      subscriptions: [],
      showAddDialog: false,
      addForm: {
        eventTypesStr: '',
        eventDest: '',
        subType: 0,
        eventLvlStr: ''
      },
      msgForm: {
        topic: '',
        url: '',
        N: 10
      },
      // Raw string lists
      queryMessagesRaw: [],
      latestMessagesRaw: [],
      
      // Pagination states
      queryPage: {
        currentPage: 1,
        pageSize: 10
      },
      latestPage: {
        currentPage: 1,
        pageSize: 10
      },

      // Detail Dialog
      detailDialogVisible: false,
      currentDetail: ''
    }
  },
  computed: {
    uniqueTopics() {
      const topics = new Set()
      this.subscriptions.forEach(sub => {
        if (sub.eventTypes) {
          const parts = sub.eventTypes.split(',')
          parts.forEach(p => topics.add(p.trim()))
        }
      })
      return Array.from(topics)
    },
    uniqueUrls() {
      const urls = new Set()
      this.subscriptions.forEach(sub => {
        if (sub.eventDest) {
          urls.add(sub.eventDest)
        }
      })
      return Array.from(urls)
    },
    // Parse raw messages into flattened events
    queryEvents() {
        return this.parseMessagesToEvents(this.queryMessagesRaw)
    },
    latestEvents() {
        return this.parseMessagesToEvents(this.latestMessagesRaw)
    },
    // Pagination logic
    pagedQueryEvents() {
        const start = (this.queryPage.currentPage - 1) * this.queryPage.pageSize
        const end = start + this.queryPage.pageSize
        return this.queryEvents.slice(start, end)
    },
    pagedLatestEvents() {
        const start = (this.latestPage.currentPage - 1) * this.latestPage.pageSize
        const end = start + this.latestPage.pageSize
        return this.latestEvents.slice(start, end)
    }
  },
  created() {
    this.fetchSubscriptions()
  },
  methods: {
    parseMessagesToEvents(rawList) {
        const events = []
        rawList.forEach(rawMsg => {
            try {
                const msgObj = JSON.parse(rawMsg)
                // Assuming msgObj is MessageEtt
                const method = msgObj.method
                const params = msgObj.params
                if (params && params.events && Array.isArray(params.events)) {
                    params.events.forEach(evt => {
                        events.push({
                            method: method,
                            status: evt.status,
                            eventType: evt.eventType,
                            eventId: evt.eventId,
                            happenTime: evt.happenTime,
                            fullDetail: JSON.stringify(evt, null, 2) // Store formatted JSON for dialog
                        })
                    })
                }
            } catch (e) {
                // If parse fails or structure doesn't match, maybe handle gracefully?
                // For now, we ignore invalid JSON or unstructured text.
                // Or create a dummy row for "Raw Content"
                console.error("Failed to parse message", e)
                events.push({
                    method: 'Unknown',
                    status: null,
                    eventType: 'Parse Error',
                    eventId: 'N/A',
                    happenTime: 'N/A',
                    fullDetail: rawMsg
                })
            }
        })
        return events
    },
    fetchSubscriptions() {
      this.loading = true
      subscribeDetect().then(res => {
        this.subscriptions = res.data
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    confirmAdd() {
      const eventTypes = this.addForm.eventTypesStr.split(',').map(s => s.trim()).filter(s => s)
      const eventLvl = this.addForm.eventLvlStr.split(',').map(s => parseInt(s.trim())).filter(n => !isNaN(n))

      const data = {
        eventTypes: eventTypes,
        eventDest: this.addForm.eventDest,
        subType: this.addForm.subType,
        eventLvl: eventLvl
      }

      subscribeAdd(data).then(() => {
        this.$message.success('订阅添加成功')
        this.showAddDialog = false
        this.fetchSubscriptions()
        this.addForm = { eventTypesStr: '', eventDest: '', subType: 0, eventLvlStr: '' }
      })
    },
    handleDelete(row) {
        let eventTypes = []
        if (row.eventTypes) {
             eventTypes = row.eventTypes.split(',').map(s => s.trim())
        }
        
        const data = {
            eventTypes: eventTypes,
            eventDest: row.eventDest,
            subType: row.subType,
            eventLvl: [row.eventLvl]
        }

        this.$confirm('确认要删除此订阅吗？', '警告', {
          confirmButtonText: '是',
          cancelButtonText: '否',
          type: 'warning'
        }).then(() => {
          subscribeDelete(data).then(() => {
            this.$message.success('删除成功')
            this.fetchSubscriptions()
          })
        })
    },
    fetchMessages() {
      getMsg(this.msgForm).then(res => {
        this.queryMessagesRaw = res.data || []
        this.queryPage.currentPage = 1
      })
    },
    fetchLatest() {
      getLatestMsg().then(res => {
        this.latestMessagesRaw = res.data || []
        this.latestPage.currentPage = 1
      })
    },
    handleQuerySizeChange(val) {
        this.queryPage.pageSize = val
    },
    handleQueryCurrentChange(val) {
        this.queryPage.currentPage = val
    },
    handleLatestSizeChange(val) {
        this.latestPage.pageSize = val
    },
    handleLatestCurrentChange(val) {
        this.latestPage.currentPage = val
    },
    showDetail(row) {
        this.currentDetail = row.fullDetail
        this.detailDialogVisible = true
    }
  }
}
</script>

<style scoped>
.msg-list {
  margin-top: 20px;
}
.empty-text {
  color: #999;
  text-align: center;
  padding: 20px;
}
pre {
  background-color: #f5f7fa;
  padding: 10px;
  border-radius: 4px;
  margin: 0;
  white-space: pre-wrap;
  word-wrap: break-word;
  max-height: 60vh;
  overflow-y: auto;
}
</style>
