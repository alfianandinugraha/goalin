import Ajv from "ajv";
import goalServices from "services/goal";
import response from "utils/constants/messages/response";
import { createGoalSchema } from "utils/constants/schemas/goal";
import { connectAuth } from "utils/helpers/connect";

const ajv = new Ajv();

export default connectAuth()
  .get(async (req, res) => {
    const { goalId } = req.query;

    try {
      const goal = await goalServices.get({ goalId, userId: req.user.id });
      return res.json({
        message: "Berhasil mendapatkan detail goal",
        payload: goal,
      });
    } catch (err) {
      return res.status(err.statusCode ?? 400).json({
        message: err.message,
        payload: {},
      });
    }
  })
  .delete(async (req, res) => {
    const { goalId } = req.query;

    try {
      await goalServices.remove({ goalId, userId: req.user.id });
      return res.json({
        message: "Berhasil menghapus goal",
        payload: {},
      });
    } catch (err) {
      return res.status(err.statusCode ?? 400).json({
        message: err.message,
        payload: {},
      });
    }
  })
  .put(async (req, res) => {
    const validate = ajv.compile(createGoalSchema);

    if (!validate(req.body)) {
      return res.status(400).json(response.INVALID_BODY);
    }

    const { goalId } = req.query;

    try {
      const goal = await goalServices.update({
        goalId,
        userId: req.user.id,
        ...req.body,
      });

      return res.json({
        message: "Berhasil memperbarui goal",
        payload: goal,
      });
    } catch (err) {
      return res.status(err.statusCode ?? 400).json({
        message: err.message,
        payload: {},
      });
    }
  });
