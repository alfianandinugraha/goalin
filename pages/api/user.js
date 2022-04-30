import { connectAuth } from "utils/helpers/connect";

export default connectAuth().get(async (req, res) => {
  return res.json({
    message: "Berhasil mendapatkan user",
    payload: { ...req.user },
  });
});
