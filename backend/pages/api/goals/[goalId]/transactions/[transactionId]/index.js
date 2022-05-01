import transactionServices from "services/transaction";
import { connectAuth } from "utils/helpers/connect";

export default connectAuth().delete(async (req, res) => {
  const { transactionId } = req.query;

  try {
    await transactionServices.remove(transactionId);
    return res.json({
      message: "Berhasil menghapus transaction",
      payload: {},
    });
  } catch (err) {
    return res.status(err.statusCode ?? 400).json({
      message: err.message,
      payload: {},
    });
  }
});
