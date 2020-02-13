import {
  findMicroService
} from './api'
import  {
  useEffect,
  useState
} from "react";

const init = async () => {
  const {
    context,
    code
  } = await findMicroService();
  if (code === 200) {
    return context
  }
  return []
}

const useInit = () => {
  const [services, setServices] = useState([])
  useEffect(() => {
    init().then(res => setServices(res))
  }, []);
  return [services];
};

export {
  useInit
}