import axios from 'axios'

  const api=
       axios.create({
        baseURL: 'http://localhost:8089',
        headers: {
          Accept: 'application/json',
          'Content-Type': 'application/json',
        },
      })
      console.log(api.config);
export default api