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
    total: body.total,
    category,
  };
};

const goalServices = {
  create: createGoal,
};

export default goalServices;
