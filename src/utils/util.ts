/**
 * 从URL获取文件
 * @param url 文件URL
 * @param fileName 文件名
 * @returns Promise<File>
 */
export const getFileFromUrl = async (url: string, fileName: string): Promise<File> => {
  const response = await fetch(url);
  const blob = await response.blob();
  return new File([blob], fileName, { type: blob.type });
}; 