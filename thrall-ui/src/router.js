import HistoryException from './components/history-exception'
import RealTimeMonitoring from './components/real-time-monitoring'

const routes = [{
    path: '/',
    exact: true,
    component: HistoryException
  },
  {
    path: '/history-exception',
    exact: false,
    component: HistoryException
  },
  {
    path: '/real-time-monitoring',
    exact: false,
    component: RealTimeMonitoring
  }
]
export default routes