import connect from "utils/helpers/connect";

export default connect().get((req, res) => {
  return res.json({
    name: "hello",
  });
});
