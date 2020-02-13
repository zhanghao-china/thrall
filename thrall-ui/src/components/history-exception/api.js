import fly from '../../fly'

const findThrallPage = (params) => {
  return fly.post("/page", params)
}
const execute = (params) => {
  return fly.post("/execute", params)
}
export {
  findThrallPage,
  execute
}