import supabase from "utils/vendors/supabase";

const getAll = async () => {
  const { data, error } = await supabase.from("wallets").select();

  if (error) {
    throw new Error("Gagal mendapatkan semua wallets");
  }

  return data.map((item) => {
    return {
      id: item.wallet_id,
      name: item.name,
    };
  });
};

const getById = async (walletId) => {
  const { data, error } = await supabase
    .from("wallets")
    .select()
    .eq("wallet_id", walletId);

  if (error) {
    throw new Error("Gagal mendapatkan wallets");
  }

  if (!data.length) {
    const error = new Error("Wallet tidak ditemukan");
    error.statusCode = 404;

    throw error;
  }

  const [wallet] = data;

  return {
    id: wallet.wallet_id,
    name: wallet.name,
  };
};

const walletServices = {
  getAll,
  get: getById,
};

export default walletServices;
