import categoryServices from "services/category";
import { connectAuth } from "utils/helpers/connect";

export default connectAuth().get(async (_, res) => {
  try {
    const goals = await categoryServices.getAll();
    return res.json({
      message: "Berhasil mendapatkan semua kategori",
      payload: goals,
    });
  } catch (err) {
    return res.status(err.statusCode ?? 400).json({
      message: err.message,
      payload: {},
    });
  }
});
