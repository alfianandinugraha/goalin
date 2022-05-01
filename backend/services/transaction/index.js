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

const transactionServices = {
  create: createTransaction,
};

export default transactionServices;
