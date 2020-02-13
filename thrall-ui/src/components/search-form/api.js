import fly from '../../fly'

const findMicroService = () => {
  return fly.get("/micro-service")
}



export {
  findMicroService
}