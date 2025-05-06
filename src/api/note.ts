import request from "../utils/request";

/**
 * 根据笔记id获取笔记
 * @param noteId 笔记id
 * @returns 笔记
 */
export const getNoteById = (noteId: string) => {
  return request<any>({
    url: "/api/note/getNoteById",
    method: "get",
    params: {
      noteId,
    },
  });
};

/**
 * 保存笔记
 * @param data 笔记实体
 * @returns 笔记id
 */
export const saveNoteByDTO = (data: any) => {
  return request<any>({
    url: "/api/note/saveNoteByDTO",
    method: "post",
    data: data,
    headers: { "Content-Type": "multipart/form-data;boundary=----WebKitFormBoundaryk4ZvuPo6pkphe7Pl" },
  });
};

/**
 * 更新笔记
 * @param data 笔记实体
 * @returns 笔记id
 */
export const updateNoteByDTO = (data: any) => {
  return request<any>({
    url: "/api/note/updateNoteByDTO",
    method: "post",
    data: data,
    headers: { "Content-Type": "multipart/form-data;boundary=----WebKitFormBoundaryk4ZvuPo6pkphe7Pl" },
  });
}; 