import supabase from "utils/vendors/supabase";
import { nanoid } from "nanoid";
import categoryServices from "services/category";

/**
 * @typedef CreateGoal
 * @property {string} name
 * @property {string} userId
 * @property {string} categoryId
 * @property {number} total
 * @property {string} notes
 */

/**
 * @param {CreateGoal} body
 */
const createGoal = async (body) => {
  const goalId = nanoid();

  const category = await categoryServices.get(body.categoryId);

  const { error } = await supabase.from("goals").insert({
    user_id: body.userId,
    name: body.name,
    notes: body.notes,
    total: body.total,
    category_id: body.categoryId,
    goal_id: goalId,
  });

  if (error) {
    throw new Error("Gagal menyimpan goal");
  }

  return {
    id: goalId,
    userId: body.userId,
    name: body.name,
    notes: body.notes,
    amount: 0,
    total: body.total,
    category,
  };
};

const getAllGoals = async (userId) => {
  const { data, error } = await supabase
    .from("goals")
    .select(
      `
        *,
        categories:category_id(
          category_id, name
        ),
        transactions(
          transaction_id, amount
        )
      `
    )
    .eq("user_id", userId);

  if (error) {
    console.log(error);
    throw new Error("Gagal mendapatkan semua goals");
  }

  return data.map((item) => {
    return {
      id: item.goal_id,
      userId: item.user_id,
      name: item.name,
      total: item.total,
      amount: item.transactions.reduce((prev, next) => prev + next.amount, 0),
      notes: item.notes,
      category: {
        id: item.categories.category_id,
        name: item.categories.name,
      },
    };
  });
};

const goalServices = {
  create: createGoal,
  getAll: getAllGoals,
};

export default goalServices;
