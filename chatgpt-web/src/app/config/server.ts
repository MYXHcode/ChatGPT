declare global {
    namespace NodeJS {
        interface ProcessEnv {
            NEXT_PUBLIC_API_HOST_URL?: string;
        }
    }
}

export const getServerSideConfig = () => {
    if (typeof process === "undefined") {
        throw Error(
            "[Server Config] you are importing a nodejs-only module outside of nodejs",
        );
    }

    return {
        apiHostUrl: process.env.NEXT_PUBLIC_API_HOST_URL
    };
};
