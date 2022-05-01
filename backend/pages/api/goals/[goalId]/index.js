import goalServices from "services/goal";
import { connectAuth } from "utils/helpers/connect";

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
  });
