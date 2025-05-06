import request from "../utils/request";

/**
 * 根据关键词获取标签
 * @param currentPage 当前页
 * @param pageSize 每页大小
 * @param keyword 关键词
 * @returns 标签列表
 */
export const getTagByKeyword = (currentPage: number, pageSize: number, keyword: string) => {
  return request<any>({
    url: `/api/tag/getTagByKeyword/${currentPage}/${pageSize}`,
    method: "get",
    params: {
      keyword
    }
  });
}; 