import { privateApi } from "../apiRequest";
import { useEffect } from "react";
import useAuth from "./useAuth";


const useAxiosPrivate = () => {
    const { token } = useAuth();
    useEffect(() => {
        const requestInterceptor = privateApi.interceptors.request.use(
            config => {
                if (!config.headers['authorization']) {
                    config.headers['authorization'] = token;
                }
                return config;
            }, (error) => Promise.reject(error)

        );
        return () => privateApi.interceptors.request.eject(requestInterceptor);

        // const responseIntercept = privateApi.interceptors.response.use(
        //     response => response,
        //     async (error) => {
        //
        //     }
        // )
    }, [token])
    return privateApi;
}

export default useAxiosPrivate;