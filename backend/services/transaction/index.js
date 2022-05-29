import { nanoid } from "nanoid";
import walletServices from "services/wallet";
import supabase from "utils/vendors/supabase";

/**
 * @typedef CreateTransactionServiceBody
 * @property {number} amount
 * @property {string} walletId
 * @property {string} goalId
 * @property {number} createdAt
 */

/**
 * @typedef RemoveTransactionServiceBody
 * @property {string} transactionId
 * @property {string} goalId
 */

/**
 * @param {CreateTransactionServiceBody} body
 */
const createTransaction = async (body) => {
  const id = nanoid();

  const wallet = await walletServices.get(body.walletId);

  const { error } = await supabase.from("transactions").insert({
    created_at: new Date(body.createdAt).toISOString(),
    wallet_id: body.walletId,
    amount: body.amount,
    goal_id: body.goalId,
    transaction_id: id,
  });

  if (error) {
    throw new Error("Gagal menyimpan transaction");
  }

  return { id, amount: body.amount, createdAt: body.createdAt, wallet };
};

const removeTransaction = async (transactionId) => {
  const { data, error } = await supabase.from("transactions").delete().match({
    transaction_id: transactionId,
  });

  if (error) {
    console.log(error);
    throw new Error("Gagal menghapus transaction");
  }

  if (!data.length) {
    const error = new Error("Transaction tidak ditemukan");
    error.statusCode = 404;

    throw error;
  }

  return {};
};

const getAllTransactions = async (goalId) => {
  const { data, error } = await supabase
    .from("transactions")
    .select(
      `
      *,
      wallet:wallet_id(
        wallet_id, name
      )
    `
    )
    .eq("goal_id", goalId)
    .order("created_at", { ascending: false });

  if (error) {
    console.log(error);
    throw new Error("Gagal mendapatkan list transaction");
  }

  return data;
};

const transactionServices = {
  create: createTransaction,
  remove: removeTransaction,
  getAll: getAllTransactions,
};

export default transactionServices;
