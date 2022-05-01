import { createClient, SupabaseClient } from "@supabase/supabase-js";

/**
 * @type {SupabaseClient}
 */
let supabase = null;

if (!supabase) {
  console.log("[Supabase]: Creating client...");
  supabase = createClient(
    process.env.NEXT_PUBLIC_SUPABASE_URL,
    process.env.NEXT_PUBLIC_SUPABASE_KEY
  );
}

export default supabase;
