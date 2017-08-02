# -*- coding: utf-8 -*-

import os
import subprocess
import tempfile
from distutils.spawn import find_executable



def job_status(job_id,
               end_point):
    '''
    Return value
    ------------
    Either doing or done.
    '''
    if job_id.strip() == "":
        return "Done"
    post_form = "form"
    cmd = ["curl",
           "-sS",
           "--insecure",
           "--%s" % post_form, "job_id=%s" % (job_id), ]
    cmd.append(end_point)
    process = subprocess.Popen(cmd,
                               stdout=subprocess.PIPE,
                               stderr=subprocess.PIPE)
    out, err = process.communicate()
    return out



def job_res(job_id,
            end_point):
    '''
    Return value
    ------------
    Either doing or done.
    '''
    if job_id.strip() == "":
        return ""
    post_form = "form"
    cmd = ["curl",
           "-sS",
           "--insecure",
           "--%s" % post_form, "job_id=%s" % (job_id), ]
    cmd.append(end_point)
    process = subprocess.Popen(cmd,
                               stdout=subprocess.PIPE,
                               stderr=subprocess.PIPE)
    out, err = process.communicate()
    return out


def submit_ocr_job(img_path,
                   end_point_submit_job,
                   langs,
                   username,
                   password):

    img_path = os.path.expanduser(img_path)
    img_path = os.path.expandvars(img_path)
    post_form = "form"
    cmd = ["curl",
           "-sS",
           "--connect-timeout", "60",
           "--max-time", "60",
           "--%s" % post_form, "file=@%s" % (
               img_path),
           "--%s" % post_form, "username=%s" % username,
           "--%s" % post_form, "password=%s" % password,
           "--insecure", ]
    i = 0
    for lang in langs:
        cmd.append("--%s" % post_form)
        cmd.append("langs[%d]=%s" % (int(i), lang))
        i += 1
    cmd.append(end_point_submit_job)

    process = subprocess.Popen(cmd,
                               stdout=subprocess.PIPE,
                               stderr=subprocess.PIPE)
    out, err = process.communicate()
    return out

if __name__ == "__main__":

    # if find_executable("curl") is None:
    #     print("please install curl according to your system")
    #     raise RuntimeError("curl not install")

    import os
    import time
    root = 'https://ocr.chongdata.com/ocr/dev_api_v2'
    end_point_submit_job = root + '/submit_job.php'
    end_point_job_status = root + '/job_status.php'
    end_point_get_res = root + '/get_res.php'

    # 请在此注册，获取账号密码
    # http://chongdata.com/old/collect_db_tasks.php
    username = "holysky5@gmail.com"
    password = "zhaotianwu"

    if username == "xxx@xxx.com":
        print("请在此注册，获取账号密码: http://chongdata.com/old/collect_db_tasks.php")
        print("并且修改username和password")
    langs = []
    langs.append("cn_sim")
    langs.append("cn_tr")
    langs.append("en")
    
    cur_dir = os.path.dirname(os.path.realpath(__file__))
    img_path = os.path.join(cur_dir, "sample10.png")

    img_paths = []
    img_paths.append(img_path)
    img_paths.append(img_path)

    print("Start to submit a job....")
    job_ids = []
    for img_path in img_paths:
        job_id = submit_ocr_job(img_path,
                                end_point_submit_job,
                                langs,
                                username,
                                password)
        job_ids.append(job_id)
    print("End to submit a job....")
    start_time = time.time()
    is_all_done = True
    is_first_loop = True
    while is_first_loop or (not is_all_done):
        time.sleep(3)
        is_all_done = True
        is_first_loop = False
        for job_id in job_ids:
            out = job_status(job_id, end_point_job_status)
            if out != "Done":
                print("Doing")
                is_all_done = False
                break
    elapsed_time = time.time() - start_time

    print("It takes " + repr(int(elapsed_time/2)) + " seconds")

    res_list = []
    for job_id in job_ids:
        out = job_res(job_id, end_point_get_res)
        print("=" * 40)
        print("job_id=", job_id)
        print("ocr res=")
        print(out)
        res_list.append(out)

