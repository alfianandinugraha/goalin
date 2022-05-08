import supabase from "utils/vendors/supabase";
import { nanoid } from "nanoid";
import categoryServices from "services/category";

/**
 * @typedef CreateGoalServiceBody
 * @property {string} name
 * @property {string} userId
 * @property {string} categoryId
 * @property {number} total
 * @property {string} notes
 */

/**
 * @typedef RemoveGoalServiceBody
 * @property {string} userId
 * @property {string} goalId
 */

/**
 * @typedef GetDetailGoalServiceBody
 * @property {string} userId
 * @property {string} goalId
 */

/**
 * @typedef UpdateGoalServiceBody
 * @property {string} name
 * @property {string} goalId
 * @property {string} userId
 * @property {string} categoryId
 * @property {number} total
 * @property {string} notes
 */

/**
 * @param {CreateGoalServiceBody} body
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
    .eq("user_id", userId)
    .order("created_at", { ascending: false });

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

/**
 * @param {GetDetailGoalServiceBody} body
 */
const getDetail = async (body) => {
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
    .eq("goal_id", body.goalId)
    .eq("user_id", body.userId);

  if (error) {
    console.log(error);
    throw new Error("Gagal mendapatkan detail goals");
  }

  if (!data.length) {
    const error = new Error("Goal tidak ditemukan");
    error.statusCode = 404;

    throw error;
  }

  const [goal] = data;

  return {
    id: goal.goal_id,
    userId: goal.user_id,
    name: goal.name,
    total: goal.total,
    amount: goal.transactions.reduce((prev, next) => prev + next.amount, 0),
    notes: goal.notes,
    category: {
      id: goal.categories.category_id,
      name: goal.categories.name,
    },
  };
};

/**
 * @param {RemoveGoalServiceBody} body
 */
const removeGoal = async (body) => {
  const { data, error } = await supabase.from("goals").delete().match({
    goal_id: body.goalId,
    user_id: body.userId,
  });

  if (error) {
    console.log(error);
    throw new Error("Gagal menghapus goal");
  }

  if (!data.length) {
    const error = new Error("Goal tidak ditemukan");
    error.statusCode = 404;

    throw error;
  }

  return {};
};

/**
 * @param {UpdateGoalServiceBody} body
 * @description Only update column name, notes, total, and category_id
 */
const updateGoal = async (body) => {
  await categoryServices.get(body.categoryId);

  const { error } = await supabase
    .from("goals")
    .update({
      name: body.name,
      notes: body.notes,
      total: body.total,
      category_id: body.categoryId,
    })
    .match({
      goal_id: body.goalId,
      user_id: body.userId,
    });

  if (error) {
    console.log(error);
    throw new Error("Gagal memperbarui goal");
  }

  const goal = await goalServices.get({
    userId: body.userId,
    goalId: body.goalId,
  });

  return goal;
};

const goalServices = {
  create: createGoal,
  getAll: getAllGoals,
  get: getDetail,
  remove: removeGoal,
  update: updateGoal,
};

export default goalServices;
