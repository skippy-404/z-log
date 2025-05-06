import { defineStore } from 'pinia';

interface UserState {
  token: string;
  userInfo: any;
}

export const useUserStore = defineStore('user', {
  state: (): UserState => ({
    token: localStorage.getItem('token') || '',
    userInfo: JSON.parse(localStorage.getItem('userInfo') || '{}')
  }),
  
  getters: {
    getToken(): string {
      return this.token;
    },
    getUserInfo(): any {
      return this.userInfo;
    },
    isLogin(): boolean {
      return !!this.token;
    }
  },
  
  actions: {
    setToken(token: string) {
      this.token = token;
      localStorage.setItem('token', token);
    },
    setUserInfo(userInfo: any) {
      this.userInfo = userInfo;
      localStorage.setItem('userInfo', JSON.stringify(userInfo));
    },
    logout() {
      this.token = '';
      this.userInfo = {};
      localStorage.removeItem('token');
      localStorage.removeItem('userInfo');
    }
  }
}); 