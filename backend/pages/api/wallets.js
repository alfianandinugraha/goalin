import walletServices from "services/wallet";
import { connectAuth } from "utils/helpers/connect";

export default connectAuth().get(async (_, res) => {
  try {
    const wallets = await walletServices.getAll();
    return res.json({
      message: "Berhasil mendapatkan semua wallets",
      payload: wallets,
    });
  } catch (err) {
    return res.status(err.statusCode ?? 400).json({
      message: err.message,
      payload: {},
    });
  }
});
