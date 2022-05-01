import supabase from "utils/vendors/supabase";

const getDetail = async (categoryId) => {
  const { data, error } = await supabase
    .from("categories")
    .select("*")
    .eq("category_id", categoryId);

  if (error) {
    throw new Error("Gagal mendapatkan kategori");
  }

  if (!data.length) {
    const error = new Error("Kategori tidak ditemukan");
    error.statusCode = 404;

    throw error;
  }

  return data.map((item) => {
    return {
      id: item.category_id,
      name: item.name,
    };
  });
};

const getAll = async () => {
  const { data, error } = await supabase.from("categories").select("*");

  if (error) {
    throw new Error("Gagal mendapatkan kategori");
  }

  return data.map((item) => {
    return {
      id: item.category_id,
      name: item.name,
    };
  });
};

const categoryServices = {
  get: getDetail,
  getAll: getAll,
};

export default categoryServices;
