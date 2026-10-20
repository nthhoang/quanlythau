import axiosClient from './axiosClient';

export async function uploadAnh(file) {
  const formData = new FormData();
  formData.append('file', file, 'anh.jpg');
  const res = await axiosClient.post('/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  });
  return res.data.url;
}
